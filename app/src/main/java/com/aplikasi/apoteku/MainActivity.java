package com.aplikasi.apoteku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        // Check if user is not logged in
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // Prevent further execution if user is redirected to login
        }

        // Midtrans SDK initialization
        SdkUIFlowBuilder.init()
                .setClientKey("SB-Mid-client-_KJOruedBfyTVnjZ") // client_key is obtained from Midtrans dashboard
                .setContext(this)
                .setMerchantBaseUrl("https://app.indit.online/midtrans.php/") // merchant_url is the server endpoint
                .enableLog(true)
                .buildSDK();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Bundle args = new Bundle();
        String username = sharedPreferences.getString("username", null);

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (id == R.id.nav_setting) {
            SettingFragment settingFragment = new SettingFragment();
            args.putString("username", username);
            settingFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settingFragment).addToBackStack(null).commit();
        } else if (id == R.id.nav_contact_us) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactUsFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_about_us) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment()).addToBackStack(null).commit();
        } else if (id == R.id.nav_log_out) {
            logoutUser();
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();

            // Hapus data sesi pengguna di SharePreference
            SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Mulai aktifitas login baru lagi
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Menutup drawer layout ketika user masuk dashboard
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("isLoggedIn");
        editor.apply();

        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        // Clear all activities and start LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }
}
