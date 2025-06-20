package com.example.rentalmobilmulia.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class MobilModel implements Serializable {

    @SerializedName("id_mobil")
    private int id_mobil;

    @SerializedName("id_merek")
    private int id_merek;

    @SerializedName("nama_mobil")
    private String nama_mobil;

    @SerializedName("nopol")
    private String nopol;

    @SerializedName("harga_sewa")
    private String harga; // akan diparse manual ke double

    @SerializedName("bahan_bakar")
    private String bb;

    @SerializedName("transmisi")
    private String transmisi;

    @SerializedName("tahun")
    private String tahun;

    @SerializedName("kapasitas")
    private String seating;

    @SerializedName("foto_mobil")
    private String image1;

    @SerializedName("image2")
    private String image2;

    @SerializedName("image3")
    private String image3;

    @SerializedName("image4")
    private String image4;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("nomesin")
    private String nomesin;

    @SerializedName("norangka")
    private String norangka;

    @SerializedName("warna")
    private String warna;

    @SerializedName("RegDate")
    private String regDate;

    @SerializedName("status")
    private String status_mobil;

    @SerializedName("total_disewa")
    private int total_disewa;

    private String merk; // optional

    // ===================== GETTER =====================
    public int getId_mobil() {
        return id_mobil;
    }

    public int getId_merek() {
        return id_merek;
    }

    public String getNama_mobil() {
        return nama_mobil;
    }

    public String getNopol() {
        return nopol;
    }

    public double getHarga_sewa() {
        try {
            if (harga == null || harga.trim().isEmpty() || harga.equalsIgnoreCase("null")) {
                return 0;
            }
            return Double.parseDouble(harga.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getBb() {
        return bb;
    }

    public String getTransmisi() {
        return transmisi;
    }

    public String getTahun() {
        return tahun;
    }

    public String getSeating() {
        return seating;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage3() {
        return image3;
    }

    public String getImage4() {
        return image4;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getNomesin() {
        return nomesin;
    }

    public String getNorangka() {
        return norangka;
    }

    public String getWarna() {
        return warna;
    }

    public String getRegDate() {
        return regDate;
    }

    public String getStatus() {
        return status_mobil;
    }

    public int getTotal_disewa() {
        return total_disewa;
    }

    public String getMerk() {
        return merk;
    }

    // ===================== SETTER =====================
    public void setId_mobil(int id_mobil) {
        this.id_mobil = id_mobil;
    }

    public void setId_merek(int id_merek) {
        this.id_merek = id_merek;
    }

    public void setNama_mobil(String nama_mobil) {
        this.nama_mobil = nama_mobil;
    }

    public void setNopol(String nopol) {
        this.nopol = nopol;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public void setBb(String bb) {
        this.bb = bb;
    }

    public void setTransmisi(String transmisi) {
        this.transmisi = transmisi;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public void setSeating(String seating) {
        this.seating = seating;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setNomesin(String nomesin) {
        this.nomesin = nomesin;
    }

    public void setNorangka(String norangka) {
        this.norangka = norangka;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public void setStatus(String status) {
        this.status_mobil = status;
    }

    public void setTotal_disewa(int total_disewa) {
        this.total_disewa = total_disewa;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    // ===================== CONSTRUCTOR TAMBAHAN =====================
    public MobilModel(int id_mobil, String nama_mobil, String transmisi, String merk, String tahun,
                      String seating, String bb, String status, String harga,
                      String deskripsi, String image1, int total_disewa) {

        this.id_mobil = id_mobil;
        this.nama_mobil = nama_mobil;
        this.transmisi = transmisi;
        this.merk = merk;
        this.tahun = tahun;
        this.seating = seating;
        this.bb = bb;
        this.status_mobil = status;
        this.harga = harga;
        this.deskripsi = deskripsi;
        this.image1 = image1;
        this.total_disewa = total_disewa;
    }

    // Optional constructor with harga_sewa as double
    public MobilModel(int id_mobil, String nama_mobil, String transmisi, String merk, String tahun,
                      String seating, String bb, String status, double harga_sewa,
                      String deskripsi, String image1, int total_disewa) {

        this.id_mobil = id_mobil;
        this.nama_mobil = nama_mobil;
        this.transmisi = transmisi;
        this.merk = merk;
        this.tahun = tahun;
        this.seating = seating;
        this.bb = bb;
        this.status_mobil = status;
        this.harga = String.valueOf(harga_sewa);
        this.deskripsi = deskripsi;
        this.image1 = image1;
        this.total_disewa = total_disewa;
    }
}
