package com.example.rentalmobilmulia.model;

public class PesananModel {
    private String id_sewa, nama_mobil, foto_mobil, tanggal_mulai, tanggal_selesai, status;
    private double total_harga;

    public String getId_sewa() { return id_sewa; }
    public String getNama_mobil() { return nama_mobil; }
    public String getFoto_mobil() { return foto_mobil; }
    public String getTanggal_mulai() { return tanggal_mulai; }
    public String getTanggal_selesai() { return tanggal_selesai; }
    public String getStatus() { return status; }
    public double getTotal_harga() { return total_harga; }

    // Setter juga bisa ditambahkan jika diperlukan
}
