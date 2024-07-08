package com.aplikasi.apoteku;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi.apoteku.adapter.ObatAdapter;
import com.aplikasi.apoteku.model.Obat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetailInfoObatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewObat;
    private ObatAdapter obatAdapter;
    private List<Obat> obatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_obat);

        recyclerViewObat = findViewById(R.id.recyclerViewObat);
        recyclerViewObat.setLayoutManager(new LinearLayoutManager(this));
        obatList = new ArrayList<>();
        obatAdapter = new ObatAdapter(this, obatList, obat -> {
            Intent intent = new Intent(DetailInfoObatActivity.this, DetailInfoObatActivity.class);
            intent.putExtra("obatId", obat.getNama());
            startActivity(intent);
        });
        recyclerViewObat.setAdapter(obatAdapter);

        loadObatData();
    }

    private void loadObatData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("obat");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                obatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Obat obat = snapshot.getValue(Obat.class);
                    obatList.add(obat);
                }
                obatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}