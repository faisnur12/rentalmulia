package com.example.rentalmobilmulia.ui.pesanan;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public static int getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        return prefs.getInt("id_user", -1);  // default -1 jika tidak ditemukan
    }

    public static void saveUserId(Context context, int idUser) {
        SharedPreferences.Editor editor = context.getSharedPreferences("user_session", Context.MODE_PRIVATE).edit();
        editor.putInt("id_user", idUser);
        editor.apply();
    }
}
