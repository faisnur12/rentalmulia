package com.example.rentalmobilmulia.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MobilResponse {
    @SerializedName("result")
    private int result;

    @SerializedName("mobil")
    private List<MobilModel> mobil;

    public int getResult() {
        return result;
    }

    public List<MobilModel> getMobil() {
        return mobil;
    }
}
