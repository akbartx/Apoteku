package com.aplikasi.apoteku;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DetailKonsultasiFragment extends Fragment {

    private String[] doctorContacts = {
            "Dr.Bedah Umum RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2018100003",
            "Dr. Anak RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2017070077",
            "Dr. Bedah Tulang RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2017060032",
            "Dr. Bedah Kulit & Kelamin RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2018110003",
            "Dr. Mata RS.Kasih Ibu: https://doctor.kih.co.id/schedule/DOK2023100003",
    };

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detail_konsultasi, container, false);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        ListView listView = view.findViewById(R.id.listViewDoctors);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_doctor, R.id.textDoctorName, doctorContacts);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openWebsite(doctorContacts[position]);
            }
        });

        return view;
    }

    private void openWebsite(String contact) {
        String url = contact.split(": ")[1];
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Tidak dapat membuka situs web", Toast.LENGTH_SHORT).show();
        }
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
