package com.aplikasi.apoteku.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
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

public class BeliObatAdapter extends RecyclerView.Adapter<BeliObatAdapter.ObatViewHolder> {

    private Context context;
    private List<Obat> obatList;
    private OnItemClickListener listener;
    private String searchText = "";

    public interface OnItemClickListener {
        void onItemClick(Obat obat);
    }

    public BeliObatAdapter(Context context, List<Obat> obatList, OnItemClickListener listener) {
        this.context = context;
        this.obatList = obatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ObatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_beli, parent, false);
        return new ObatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObatViewHolder holder, int position) {
        Obat obat = obatList.get(position);
        holder.bind(obat, listener, searchText);
    }

    @Override
    public int getItemCount() {
        return obatList.size();
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
        notifyDataSetChanged();
    }

    public static class ObatViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewObat;
        TextView textViewNamaObat;
        TextView textViewHargaObat;

        public ObatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewObat = itemView.findViewById(R.id.imageViewObat);
            textViewNamaObat = itemView.findViewById(R.id.textViewNamaObat);
            textViewHargaObat = itemView.findViewById(R.id.textViewHargaObat);
        }

        public void bind(final Obat obat, final OnItemClickListener listener, String searchText) {
            textViewNamaObat.setText(highlightSearchText(obat.getNama(), searchText));
            textViewHargaObat.setText("Rp. " + obat.getHarga());

            Glide.with(itemView.getContext()).load(obat.getGambarUrl()).into(imageViewObat);
            itemView.setOnClickListener(v -> listener.onItemClick(obat));
        }

        private SpannableString highlightSearchText(String originalText, String searchText) {
            SpannableString spannableString = new SpannableString(originalText);
            if (!searchText.isEmpty()) {
                int startPos = originalText.toLowerCase().indexOf(searchText.toLowerCase());
                if (startPos != -1) {
                    int endPos = startPos + searchText.length();
                    spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return spannableString;
        }
    }
}
