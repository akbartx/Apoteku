package com.aplikasi.apoteku;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi.apoteku.adapter.BeliObatAdapter;
import com.aplikasi.apoteku.model.Obat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailBeliObatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BeliObatAdapter obatAdapter;
    private List<Obat> obatList;
    private List<Obat> filteredList;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_beli_obat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchEditText = findViewById(R.id.searchEditText);

        obatList = new ArrayList<>();
        filteredList = new ArrayList<>();

        obatAdapter = new BeliObatAdapter(this, filteredList, obat -> startMidtransPayment(obat));

        recyclerView = findViewById(R.id.recyclerViewObat);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(obatAdapter);

        fetchDataFromFirebase();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
                obatAdapter.setSearchText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void startMidtransPayment(Obat obat) {
        Intent intent = new Intent(this, MidtransPaymentActivity.class);
        intent.putExtra("obat", obat);
        startActivity(intent);
    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("obat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                obatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Obat obat = snapshot.getValue(Obat.class);
                    obatList.add(obat);
                }
                filteredList.clear();
                filteredList.addAll(obatList);
                obatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void filter(String text) {
        filteredList.clear();
        for (Obat item : obatList) {
            if (item.getNama().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        obatAdapter.notifyDataSetChanged();
    }
}
