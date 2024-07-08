package com.aplikasi.apoteku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailPusatBantuanActivity extends AppCompatActivity {

    private String[] doctorContacts = {
            "Dr.Bedah Umum RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2018100003",
            "Dr. Anak RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2017070077",
            "Dr. Bedah Tulang RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2017060032",
            "Dr. Bedah Kulit & Kelamin RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2018110003",
            "Dr. Mata RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2023100003",
    };

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_konsultasi); // Update with the correct layout if needed

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        ListView listView = findViewById(R.id.listViewDoctors);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_doctor, R.id.textDoctorName, doctorContacts);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openWebsite(doctorContacts[position]);
            }
        });
    }

    private void openWebsite(String contact) {
        String url = contact.split(": ")[1];
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak dapat membuka situs web", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
