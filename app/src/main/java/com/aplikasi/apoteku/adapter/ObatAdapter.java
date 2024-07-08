package com.aplikasi.apoteku.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aplikasi.apoteku.R;
import com.aplikasi.apoteku.model.Obat;
import com.bumptech.glide.Glide;
import java.util.List;

public class ObatAdapter extends RecyclerView.Adapter<ObatAdapter.ObatViewHolder> {

    private Context context;
    private List<Obat> obatList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Obat obat);
    }

    public ObatAdapter(Context context, List<Obat> obatList, OnItemClickListener listener) {
        this.context = context;
        this.obatList = obatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ObatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_obat, parent, false);
        return new ObatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObatViewHolder holder, int position) {
        Obat obat = obatList.get(position);
        holder.bind(obat, listener);
    }

    @Override
    public int getItemCount() {
        return obatList.size();
    }

    public static class ObatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewObat;
        TextView textViewNamaObat;
        TextView textViewDeskripsiObat;
        TextView textViewHargaObat;

        public ObatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewObat = itemView.findViewById(R.id.imageViewObat);
            textViewNamaObat = itemView.findViewById(R.id.textViewNamaObat);
            textViewDeskripsiObat = itemView.findViewById(R.id.textViewDeskripsiObat);
            textViewHargaObat = itemView.findViewById(R.id.textViewHargaObat); // tambahkan inisialisasi harga
        }

        public void bind(final Obat obat, final OnItemClickListener listener) {
            textViewNamaObat.setText(obat.getNama());
            textViewDeskripsiObat.setText(obat.getDeskripsi());
            textViewHargaObat.setText("Rp. " + obat.getHarga()); // set teks harga

            Glide.with(itemView.getContext()).load(obat.getGambarUrl()).into(imageViewObat);
            itemView.setOnClickListener(v -> listener.onItemClick(obat));
        }
    }
}
