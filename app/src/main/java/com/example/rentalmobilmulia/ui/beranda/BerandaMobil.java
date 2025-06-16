package com.example.rentalmobilmulia.ui.beranda;

import java.io.Serializable;

public class BerandaMobil implements Serializable {
    private String id_mobil;
    private String nama_mobil;
    private String transmisi;
    private String merk;
    private String tahun;
    private String kapasitas;
    private String bahan_bakar;
    private String status;
    private String harga_sewa;
    private String deskripsi;
    private String foto_mobil;
    private int total_disewa;

    public String getId_mobil() {
        return id_mobil;
    }

    public void setId_mobil(String id_mobil) {
        this.id_mobil = id_mobil;
    }

    public String getNama_mobil() {
        return nama_mobil;
    }

    public void setNama_mobil(String nama_mobil) {
        this.nama_mobil = nama_mobil;
    }

    public String getTransmisi() {
        return transmisi;
    }

    public void setTransmisi(String transmisi) {
        this.transmisi = transmisi;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public String getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(String kapasitas) {
        this.kapasitas = kapasitas;
    }

    public String getBahan_bakar() {
        return bahan_bakar;
    }

    public void setBahan_bakar(String bahan_bakar) {
        this.bahan_bakar = bahan_bakar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getHarga_sewa() {
        try {
            return Double.parseDouble(harga_sewa);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setHarga_sewa(String harga_sewa) {
        this.harga_sewa = harga_sewa;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getFoto_mobil() {
        return foto_mobil;
    }

    public void setFoto_mobil(String foto_mobil) {
        this.foto_mobil = foto_mobil;
    }

    public int getTotal_disewa() {
        return total_disewa;
    }

    public void setTotal_disewa(int total_disewa) {
        this.total_disewa = total_disewa;
    }

}
