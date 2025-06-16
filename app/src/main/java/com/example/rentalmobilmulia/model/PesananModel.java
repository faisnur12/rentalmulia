package com.example.rentalmobilmulia.model;

public class PesananModel {
    private String kode_booking;
    private int id_mobil;
    private String nama_mobil;
    private String foto_mobil;
    private String tgl_mulai;
    private String tgl_selesai;
    private int durasi;
    private double total;
    private String status;

    public String getKode_booking() {
        return kode_booking;
    }

    public int getId_mobil() {
        return id_mobil;
    }

    public String getNama_mobil() {
        return nama_mobil;
    }

    public String getFoto_mobil() {
        return foto_mobil;
    }

    public String getTgl_mulai() {
        return tgl_mulai;
    }

    public String getTgl_selesai() {
        return tgl_selesai;
    }

    public int getDurasi() {
        return durasi;
    }

    public double getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }
}
