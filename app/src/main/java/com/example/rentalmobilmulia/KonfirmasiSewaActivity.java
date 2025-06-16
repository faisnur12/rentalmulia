package com.example.rentalmobilmulia;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.model.ResponseSewa;


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfirmasi_sewa);

        initViews();
        getIntentData();
    }

    private void initViews() {
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
    }

    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idMobil = extras.getInt("id_mobil", -1);
            String namaMobil = extras.getString("nama_mobil", "Mobil");
            double hargaSewa = extras.getDouble("harga_sewa", 0);
            tanggalMulai = extras.getString("tanggal_mulai", "");
            tanggalSelesai = extras.getString("tanggal_selesai", "");
            metodePickup = extras.getString("metode_pickup", "Ambil Sendiri");
            driver = extras.getString("driver", "Tidak");
            String fotoMobil = extras.getString("foto_mobil", "");

            // Validasi tanggal
            int hariSewa = hitungHariSewa(tanggalMulai, tanggalSelesai);
            if (hariSewa <= 0) {
                Toast.makeText(this, "Tanggal sewa tidak valid!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Hitung biaya
            double biayaMobil = hargaSewa * hariSewa;
            int biayaDriver = driver.equalsIgnoreCase("Ya") ? 450000 * hariSewa : 0;
            totalHarga = biayaMobil + biayaDriver;

            // Set tampilan
            tvNamaMobil.setText(namaMobil);
            tvHargaSewa.setText(formatRupiah(hargaSewa) + " / Hari");
            tvTanggalMulai.setText(tanggalMulai);
            tvTanggalSelesai.setText(tanggalSelesai);
            tvMetodePickup.setText(metodePickup);
            tvBiayaMobil.setText(formatRupiah(biayaMobil));
            tvBiayaDriver.setText(formatRupiah(biayaDriver));
            tvTotalHarga.setText(formatRupiah(totalHarga));
            tvLamaSewa.setText(hariSewa + " Hari");

            Glide.with(this)
                    .load(BASE_URL_FOTO + fotoMobil)
                    .placeholder(R.drawable.sample_mobil)
                    .error(R.drawable.sample_mobil)
                    .into(imgMobil);

            btnKonfirmasi.setOnClickListener(v -> konfirmasiSewa());
        } else {
            Toast.makeText(this, "Data sewa tidak ditemukan!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void konfirmasiSewa() {
        SharedPreferences pref = getSharedPreferences("login_pref", MODE_PRIVATE);
        String email = pref.getString("email", "");

        if (email.isEmpty()) {
            Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show();
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
            public void onResponse(@NonNull Call<ResponseSewa> call, @NonNull Response<ResponseSewa> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getSuccess() == 1) {
                        Toast.makeText(KonfirmasiSewaActivity.this, "Sewa berhasil dikonfirmasi!", Toast.LENGTH_SHORT).show();

                        String kodeBooking = response.body().getKodeBooking();

                        Intent intent = new Intent(KonfirmasiSewaActivity.this, BuktiSewaActivity.class);
                        intent.putExtra("kode_booking", kodeBooking);
                        intent.putExtra("total_harga", totalHarga);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(KonfirmasiSewaActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(KonfirmasiSewaActivity.this, "Gagal menyimpan data sewa!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(@NonNull Call<ResponseSewa> call, @NonNull Throwable t) {
                Toast.makeText(KonfirmasiSewaActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private int hitungHariSewa(String mulai, String selesai) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date dateMulai = format.parse(mulai);
            Date dateSelesai = format.parse(selesai);
            long diff = dateSelesai.getTime() - dateMulai.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24)); // +1 termasuk hari pertama
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
