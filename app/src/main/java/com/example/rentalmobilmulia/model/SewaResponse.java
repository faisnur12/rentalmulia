package com.example.rentalmobilmulia.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SewaResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("sewa")
    private List<SewaModel> sewa;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public List<SewaModel> getSewa() {
        return sewa;
    }
}
