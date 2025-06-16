package com.example.rentalmobilmulia.model;

import com.google.gson.annotations.SerializedName;

public class ResponseSewa {

    @SerializedName("success")
    private int success;

    @SerializedName("message")
    private String message;

    @SerializedName("kode_booking")
    private String kodeBooking;

    public ResponseSewa(int success, String message, String kodeBooking) {
        this.success = success;
        this.message = message;
        this.kodeBooking = kodeBooking;
    }

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getKodeBooking() {
        return kodeBooking;
    }
}
