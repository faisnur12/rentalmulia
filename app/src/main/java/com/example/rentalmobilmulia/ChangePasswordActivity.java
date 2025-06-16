package com.example.rentalmobilmulia;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    public static final String URL = new RetrofitClient().BASE_URL;

    EditText etEmailChangePassword, etOldPassword, etNewPassword, etConfirmNewPassword;
    Button btnChangePassword;
    SharedPreferences sharedPreferences; // Tetap diperlukan untuk pre-fill email jika ada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etEmailChangePassword = findViewById(R.id.etEmailChangePassword);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        sharedPreferences = getSharedPreferences("login_Pref", MODE_PRIVATE);

        // Mengisi otomatis field email jika pengguna sudah login
        // Jika tidak login, field akan kosong dan pengguna harus memasukkannya secara manual
        String loggedInEmail = sharedPreferences.getString("email", "");
        if (!loggedInEmail.isEmpty()) {
            etEmailChangePassword.setText(loggedInEmail);
            // Anda bisa membuatnya tidak bisa diedit jika ingin
            // etEmailChangePassword.setEnabled(false);
            // etEmailChangePassword.setFocusable(false);
            // etEmailChangePassword.setFocusableInTouchMode(false);
        }
        // >>> HILANGKAN BLOK INI:
        // else {
        //     Toast.makeText(this, "Silakan login terlebih dahulu.", Toast.LENGTH_LONG).show();
        //     finish();
        //     return;
        // }


        btnChangePassword.setOnClickListener(view -> {
            String email = etEmailChangePassword.getText().toString().trim();
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();
            String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();

            if (email.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(ChangePasswordActivity.this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmNewPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Password Baru dan Konfirmasi Password tidak cocok", Toast.LENGTH_SHORT).show();
            } else {
                // PERHATIAN SANGAT PENTING:
                // Meskipun Anda tidak diwajibkan login di Activity ini,
                // proses di sisi SERVER (change_password.php) TETAP MEMBUTUHKAN password lama yang benar
                // untuk email tersebut agar perubahan password berhasil.
                // Jika Anda ingin reset password tanpa mengetahui password lama,
                // gunakan fitur 'Lupa Password' (Forgot Password) yang mengirim link ke email.
                changePassword(email, oldPassword, newPassword);
            }
        });
    }

    private void changePassword(String email, String oldPassword, String newPassword) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerAPI api = retrofit.create(ServerAPI.class);

        api.changePassword(email, oldPassword, newPassword).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() == null) {
                        Toast.makeText(ChangePasswordActivity.this, "Response kosong dari server", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    Log.e("SERVER_RESPONSE", responseBody);  // ← ini WAJIB untuk debug

                    if (json.getString("result").equals("1")) {
                        showInfoDialog("Password berhasil diubah!");
                        finish(); // Kembali ke activity sebelumnya setelah berhasil
                    } else {
                        showErrorDialog(json.getString("message"));
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    showErrorDialog("Terjadi Kesalahan: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showErrorDialog("Gagal Terhubung ke Server: " + t.getMessage());
            }
        });
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(ChangePasswordActivity.this)
                .setMessage(message)
                .setNegativeButton("OK", null)
                .create()
                .show();
    }

    private void showInfoDialog(String message) {
        new AlertDialog.Builder(ChangePasswordActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }
}