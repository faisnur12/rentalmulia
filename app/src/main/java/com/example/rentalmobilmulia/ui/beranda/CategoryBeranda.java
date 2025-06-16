package com.example.rentalmobilmulia.ui.beranda;

public class CategoryBeranda {
    private int iconResId;
    private String nama;      // Contoh: "Toyota"
    private String idMerek;   // Contoh: "15"

    public CategoryBeranda(int iconResId, String nama, String idMerek) {
        this.iconResId = iconResId;
        this.nama = nama;
        this.idMerek = idMerek;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getNama() {
        return nama;
    }

    public String getIdMerek() {
        return idMerek;
    }
}
