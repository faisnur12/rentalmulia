package com.example.rentalmobilmulia.model;

import com.google.gson.annotations.SerializedName;

public class ResponseDefault {

    @SerializedName("success")
    private int success;

    @SerializedName("message")
    private String message;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
