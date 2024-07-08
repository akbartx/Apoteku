package com.aplikasi.apoteku.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Obat implements Parcelable {
    private String nama;
    private String deskripsi;
    private String gambarUrl;
    private int harga; // tambahkan atribut harga

    public Obat() {
        // Diperlukan konstruktor kosong untuk Firebase Database
    }

    public Obat(String nama, String deskripsi, String gambarUrl, int harga) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.gambarUrl = gambarUrl;
        this.harga = harga;
    }

    protected Obat(Parcel in) {
        nama = in.readString();
        deskripsi = in.readString();
        gambarUrl = in.readString();
        harga = in.readInt();
    }

    public static final Creator<Obat> CREATOR = new Creator<Obat>() {
        @Override
        public Obat createFromParcel(Parcel in) {
            return new Obat(in);
        }

        @Override
        public Obat[] newArray(int size) {
            return new Obat[size];
        }
    };

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambarUrl() {
        return gambarUrl;
    }

    public void setGambarUrl(String gambarUrl) {
        this.gambarUrl = gambarUrl;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nama);
        dest.writeString(deskripsi);
        dest.writeString(gambarUrl);
        dest.writeInt(harga);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
