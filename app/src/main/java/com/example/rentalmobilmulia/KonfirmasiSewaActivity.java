package com.example.rentalmobilmulia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.model.ResponseSewa;
import com.example.rentalmobilmulia.ui.pesanan.PesananFragment;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class KonfirmasiSewaActivity extends AppCompatActivity {

    private ImageView imgMobil;
    private TextView tvNamaMobil, tvHargaSewa, tvTanggalMulai, tvTanggalSelesai,
            tvMetodePickup, tvBiayaMobil, tvBiayaDriver, tvTotalHarga, tvLamaSewa;
    private Button btnKonfirmasi;

    private static final String BASE_URL = "http://10.0.2.2/API_Rental_Mulia/";
    private static final String BASE_URL_FOTO = BASE_URL + "uploads/";

    private int idMobil;
    private String tanggalMulai, tanggalSelesai, metodePickup, driver;
    private double totalHarga;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_sewa);

        // Inisialisasi UI
        imgMobil = findViewById(R.id.imgMobil);
        tvNamaMobil = findViewById(R.id.tvNamaMobil);
        tvHargaSewa = findViewById(R.id.tvHargaSewa);
        tvTanggalMulai = findViewById(R.id.tvTanggalMulai);
        tvTanggalSelesai = findViewById(R.id.tvTanggalSelesai);
        tvMetodePickup = findViewById(R.id.tvMetodePickup);
        tvBiayaMobil = findViewById(R.id.tvBiayaMobil);
        tvBiayaDriver = findViewById(R.id.tvBiayaDriver);
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        tvLamaSewa = findViewById(R.id.tvLamaSewa);
        btnKonfirmasi = findViewById(R.id.btnKonfirmasi);

        // Ambil data dari Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idMobil = extras.getInt("id_mobil");
            String namaMobil = extras.getString("nama_mobil");
            double hargaSewa = extras.getDouble("harga_sewa");
            tanggalMulai = extras.getString("tanggal_mulai");
            tanggalSelesai = extras.getString("tanggal_selesai");
            metodePickup = extras.getString("metode_pickup");
            driver = extras.getString("driver");
            String fotoMobil = extras.getString("foto_mobil");

            tvNamaMobil.setText(namaMobil);
            tvHargaSewa.setText(formatRupiah(hargaSewa) + " / Hari");
            tvTanggalMulai.setText(tanggalMulai);
            tvTanggalSelesai.setText(tanggalSelesai);
            tvMetodePickup.setText(metodePickup);

            int hariSewa = hitungHariSewa(tanggalMulai, tanggalSelesai);
            if (hariSewa <= 0) {
                Toast.makeText(this, "Tanggal selesai tidak valid!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            tvLamaSewa.setText(hariSewa + " Hari");

            double biayaMobil = hargaSewa * hariSewa;
            int biayaDriver = "Ya".equalsIgnoreCase(driver) ? 45+0000 * hariSewa : 0;
            totalHarga = biayaMobil + biayaDriver;

            tvBiayaMobil.setText(formatRupiah(biayaMobil));
            tvBiayaDriver.setText(formatRupiah(biayaDriver));
            tvTotalHarga.setText(formatRupiah(totalHarga));

            // Load gambar mobil
            Glide.with(this)
                    .load(BASE_URL_FOTO + fotoMobil)
                    .placeholder(R.drawable.sample_mobil)
                    .error(R.drawable.sample_mobil)
                    .into(imgMobil);

            btnKonfirmasi.setOnClickListener(v -> konfirmasiSewa());
        }
    }

    private void konfirmasiSewa() {
        // Ambil email dari SharedPreferences login
        SharedPreferences pref = getSharedPreferences("login_pref", MODE_PRIVATE);
        String email = pref.getString("email", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "Anda belum login!", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerAPI apiService = retrofit.create(ServerAPI.class);

        Call<ResponseSewa> call = apiService.postSewa(
                email,
                idMobil,
                tanggalMulai,
                tanggalSelesai,
                metodePickup,
                driver,
                totalHarga
        );

        call.enqueue(new Callback<ResponseSewa>() {
            @Override
            public void onResponse(Call<ResponseSewa> call, Response<ResponseSewa> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getSuccess() == 1) {
                        Toast.makeText(KonfirmasiSewaActivity.this, "Sewa berhasil dikonfirmasi!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(KonfirmasiSewaActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(KonfirmasiSewaActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(KonfirmasiSewaActivity.this, "Gagal menyimpan sewa!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSewa> call, Throwable t) {
                Toast.makeText(KonfirmasiSewaActivity.this, "Terjadi kesalahan: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int hitungHariSewa(String mulai, String selesai) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateMulai = format.parse(mulai);
            Date dateSelesai = format.parse(selesai);
            long diff = dateSelesai.getTime() - dateMulai.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24)); // tanpa +1
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private String formatRupiah(double amount) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        return format.format(amount);
    }
}
