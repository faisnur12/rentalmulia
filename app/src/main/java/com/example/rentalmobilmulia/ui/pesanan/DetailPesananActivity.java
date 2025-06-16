package com.example.rentalmobilmulia.ui.pesanan;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.model.PesananModel;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailPesananActivity extends AppCompatActivity {

    private ImageView imgMobil;
    private TextView tvNamaMobil, tvKodeBooking, tvTanggal, tvDurasi, tvStatus, tvTotal;

    public static final String BASE_IMAGE_URL = "http://10.0.2.2/API_Rental_Mulia/uploads/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        imgMobil = findViewById(R.id.imgMobil);
        tvNamaMobil = findViewById(R.id.tvNamaMobil);
        tvKodeBooking = findViewById(R.id.tvKodeBooking);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvDurasi = findViewById(R.id.tvDurasi);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotal = findViewById(R.id.tvTotal);

        // Ambil data dari intent
        String kode = getIntent().getStringExtra("kode_booking");

        // Kamu bisa fetch detail by kode_booking dari server.
        // Sementara, kalau kamu sudah passing data PesananModel bisa langsung seperti ini:
        PesananModel pesanan = (PesananModel) getIntent().getSerializableExtra("pesanan");

        if (pesanan != null) {
            tvNamaMobil.setText(pesanan.getNama_mobil());
            tvKodeBooking.setText("Kode Booking: " + pesanan.getKode_booking());
            tvTanggal.setText(pesanan.getTgl_mulai() + " - " + pesanan.getTgl_selesai());
            tvDurasi.setText("Durasi: " + pesanan.getDurasi() + " Hari");
            tvStatus.setText("Status: " + pesanan.getStatus());
            tvTotal.setText("Total: " + formatRupiah(pesanan.getTotal()));

            Glide.with(this)
                    .load(BASE_IMAGE_URL + pesanan.getFoto_mobil())
                    .placeholder(R.drawable.sample_mobil)
                    .into(imgMobil);
        }
    }

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(amount);
    }
}