package com.example.rentalmobilmulia.model;

import java.util.List;

public class ResponsePesanan {
    private int success;
    private String message;
    private List<PesananModel> pesanan;

    public int getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<PesananModel> getPesanan() {
        return pesanan;
    }
}
