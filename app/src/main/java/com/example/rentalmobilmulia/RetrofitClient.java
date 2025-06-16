package com.example.rentalmobilmulia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "http://10.0.2.2/API_Rental_Mulia/";
    public static final String BASE_URL_IMAGE = BASE_URL + "uploads/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Gunakan Gson dengan setLenient untuk mengizinkan JSON tidak sempurna
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    // Shortcut untuk akses ServerAPI
    public static ServerAPI getInstance() {
        return getClient().create(ServerAPI.class);
    }

}
