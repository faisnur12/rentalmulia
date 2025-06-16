package com.example.rentalmobilmulia.ui.profil;

public class DataPelanggan {
    private String email;
    private String nama_user;
    private String telp;
    private String alamat;
    private String ktp;
    private String kk;
    private String profile_image; // ✅ Tambahan: nama file foto profil

    // Constructor opsional
    public DataPelanggan() {}

    // Constructor tambahan jika dibutuhkan
    public DataPelanggan(String email, String nama_user, String alamat, String telp) {
        this.email = email;
        this.nama_user = nama_user;
        this.alamat = alamat;
        this.telp = telp;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public void setTelp(String telp) {
        this.telp = telp;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public void setKtp(String ktp) {
        this.ktp = ktp;
    }

    public void setKk(String kk) {
        this.kk = kk;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getNama_user() {
        return nama_user;
    }

    public String getTelp() {
        return telp;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getKtp() {
        return ktp;
    }

    public String getKk() {
        return kk;
    }

    public String getProfile_image() {
        return profile_image;
    }
}
