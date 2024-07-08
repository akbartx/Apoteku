package com.aplikasi.apoteku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {
    private CardView cardKonsultasi, cardBeliObat, cardInfoObat, cardPusatBantuan;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Deklarasi id card view pada layout
        cardKonsultasi = view.findViewById(R.id.konsultasi_card);
        cardBeliObat = view.findViewById(R.id.beli_obat_card);
        cardInfoObat = view.findViewById(R.id.info_obat_card);
        cardPusatBantuan = view.findViewById(R.id.pusat_bantuan_card);

        // Action Konsultasi card
        cardKonsultasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mengganti fragment_container dengan ConsultationFragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new DetailKonsultasiFragment());
                transaction.addToBackStack(null);  // menambahkan transaksi ke back stack sehingga bisa kembali ke HomeFragment
                transaction.commit();
            }
        });

        // Action Beli Obat card
        cardBeliObat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailBeliObatActivity.class);
                startActivity(intent);
            }
        });

        // Action Info Obat card
        cardInfoObat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailInfoObatActivity.class);
                startActivity(intent);
            }
        });

        // Action Pusat Bantuan card
        cardPusatBantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailPusatBantuanActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }
}
