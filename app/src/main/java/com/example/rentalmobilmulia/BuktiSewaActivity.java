package com.example.rentalmobilmulia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import com.example.rentalmobilmulia.model.ResponseSewa;
import com.example.rentalmobilmulia.utils.FileUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuktiSewaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView tvKodeBooking, tvStatus, tvTotalHarga, tvRekening;
    private ImageView imgBukti;
    private Button btnUploadBukti, btnSubmitBukti;

    private Uri imageUri;
    private File imageFile;
    private String kodeBooking;
    private double totalHarga;

    private ServerAPI apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bukti_sewa);

        // Inisialisasi view
        tvKodeBooking = findViewById(R.id.tvKodeBooking);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotalHarga = findViewById(R.id.tvTotalHarga);
        tvRekening = findViewById(R.id.tvRekening);
        imgBukti = findViewById(R.id.imgBukti);
        btnUploadBukti = findViewById(R.id.btnUploadBukti);
        btnSubmitBukti = findViewById(R.id.btnSubmitBukti);

        // Ambil data dari Intent
        kodeBooking = getIntent().getStringExtra("kode_booking");
        totalHarga = getIntent().getDoubleExtra("total_harga", 0);

        tvKodeBooking.setText("Kode Booking: " + kodeBooking);
        tvTotalHarga.setText("Total: " + formatRupiah(totalHarga));
        tvStatus.setText("Status: Menunggu Pembayaran");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ServerAPI.class);

        // Tombol pilih bukti
        btnUploadBukti.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Tombol submit bukti
        btnSubmitBukti.setOnClickListener(v -> {
            if (imageFile == null) {
                Toast.makeText(this, "Silakan pilih bukti transfer terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody kodeBookingPart = RequestBody.create(MediaType.parse("text/plain"), kodeBooking);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("bukti_bayar", imageFile.getName(), requestFile);

            uploadBukti(kodeBookingPart, filePart);
        });
    }

    private void uploadBukti(RequestBody kodeBooking, MultipartBody.Part filePart) {
        Call<ResponseSewa> call = apiService.uploadBuktiTransfer(kodeBooking, filePart);
        call.enqueue(new Callback<ResponseSewa>() {
            @Override
            public void onResponse(Call<ResponseSewa> call, Response<ResponseSewa> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    Toast.makeText(BuktiSewaActivity.this, "Bukti berhasil dikirim!", Toast.LENGTH_SHORT).show();
                    tvStatus.setText("Status: Sudah Dibayar");
                } else {
                    Toast.makeText(BuktiSewaActivity.this, "Gagal mengirim bukti", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseSewa> call, Throwable t) {
                Toast.makeText(BuktiSewaActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imgBukti.setImageURI(imageUri);
            imageFile = FileUtils.getFile(this, imageUri);
        }
    }

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(amount);
    }
}
