package com.example.rentalmobilmulia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    public static final String URL = new RetrofitClient().BASE_URL;

    private Button btnLogin;
    private EditText etEmail, etPassword;
    private TextView tvRegister, lupaPassword, tvLoginGuest;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setup SharedPreferences
        sharedPreferences = getSharedPreferences("login_pref", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Jika sudah login, langsung ke MainActivity
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
            return;
        }

        // Inisialisasi UI
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.tvRegister);
        lupaPassword = findViewById(R.id.LupaPassword);
        tvLoginGuest = findViewById(R.id.tvLoginGuest);
        btnLogin = findViewById(R.id.btnLogin);

        // Animasi logo
        ImageView imgLogo = findViewById(R.id.imgLogo);
        Animation blink = AnimationUtils.loadAnimation(this, R.anim.blink_logo);
        imgLogo.startAnimation(blink);

        // Aksi: Register
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Register.class));
        });

        // Aksi: Lupa password
        lupaPassword.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, ChangePasswordActivity.class));
        });

        // Aksi: Login sebagai Guest
        tvLoginGuest.setOnClickListener(v -> {
            editor.putBoolean("isLoggedIn", false);
            editor.putString("nama", "Guest");
            editor.putString("email", "");
            editor.putString("profile_image", "");
            editor.apply();

            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        });

        // Aksi: Login
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Email wajib diisi");
                etEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                etPassword.setError("Password wajib diisi");
                etPassword.requestFocus();
                return;
            }

            prosesLogin(email, password);
        });
    }

    private void prosesLogin(String email, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerAPI api = retrofit.create(ServerAPI.class);

        api.loginUser(email, password).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        showErrorDialog("Login gagal: Response tidak valid");
                        return;
                    }

                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);

                    if (json.getString("result").equals("1")) {
                        JSONObject data = json.getJSONObject("data");

                        // Simpan ke SharedPreferences
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("nama", data.getString("nama_user")); // sesuaikan dengan key JSON dari PHP
                        editor.putString("email", data.getString("email"));
                        editor.putString("profile_image", data.optString("profile_image", ""));
                        editor.apply();

                        Toast.makeText(Login.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();

                    } else {
                        showErrorDialog(json.optString("message", "Email atau Password salah"));
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    showErrorDialog("Terjadi kesalahan: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showErrorDialog("Gagal terhubung ke server: " + t.getMessage());
            }
        });
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(Login.this)
                .setTitle("Login Gagal")
                .setMessage(message)
                .setNegativeButton("Coba Lagi", null)
                .create()
                .show();
    }
}
