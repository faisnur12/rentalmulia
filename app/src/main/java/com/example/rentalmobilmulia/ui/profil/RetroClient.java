package com.example.rentalmobilmulia.ui.profil;

import com.example.rentalmobilmulia.ui.profil.ServerAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {

    private static final String BASE_URL = ServerAPI.BASE_URL_IMAGE;

    private static Retrofit retrofit = null;

    public static ServerAPI getApiServices() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ServerAPI.class);
    }
}
