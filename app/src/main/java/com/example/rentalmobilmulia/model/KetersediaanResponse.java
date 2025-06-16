package com.example.rentalmobilmulia.model;

import com.google.gson.annotations.SerializedName;

public class KetersediaanResponse {

    @SerializedName("tersedia")
    private boolean tersedia;

    public boolean isTersedia() {
        return tersedia;
    }

    public void setTersedia(boolean tersedia) {
        this.tersedia = tersedia;
    }
}
