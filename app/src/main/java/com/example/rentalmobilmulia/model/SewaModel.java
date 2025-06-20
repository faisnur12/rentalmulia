package com.example.rentalmobilmulia.model;

import com.google.gson.annotations.SerializedName;

public class SewaModel {

    @SerializedName("kode_booking")
    private String kodeBooking;

    @SerializedName("nama_mobil")
    private String namaMobil;

    @SerializedName("foto_mobil")
    private String fotoMobil;

    @SerializedName("tgl_mulai")
    private String tglMulai;

    @SerializedName("tgl_selesai")
    private String tglSelesai;

    @SerializedName("durasi")
    private String durasi;

    @SerializedName("driver")
    private String driver;

    @SerializedName("nama_sopir")
    private String namaSopir;

    @SerializedName("status")
    private String status;

    @SerializedName("bukti_bayar")
    private String buktiBayar;

    @SerializedName("total")
    private String total;

    @SerializedName("pickup")
    private String pickup;

    @SerializedName("tgl_booking")
    private String tglBooking;

    // Getter dan Setter
    public String getKodeBooking() { return kodeBooking; }
    public String getNamaMobil() { return namaMobil; }
    public String getFotoMobil() { return fotoMobil; }
    public String getTglMulai() { return tglMulai; }
    public String getTglSelesai() { return tglSelesai; }
    public String getDurasi() { return durasi; }
    public String getDriver() { return driver; }
    public String getNamaSopir() { return namaSopir; }
    public String getStatus() { return status; }
    public String getBuktiBayar() { return buktiBayar; }
    public String getTotal() { return total; }
    public String getPickup() { return pickup; }
    public String getTglBooking() { return tglBooking; }

    public void setFotoMobil(String fotoMobil) {
        this.fotoMobil = fotoMobil;
    }
}
