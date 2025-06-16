package com.example.rentalmobilmulia;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.*;
import retrofit2.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    EditText etNama, etEmail, etPassword, etTelp, etAlamat;
    LinearLayout layoutUploadKtp, layoutUploadKk;
    ImageView ivPreviewKtp, ivPreviewKk;
    Button etSubmit;

    Uri uriKtp, uriKk, cameraUri;
    final int PICK_KTP = 101;
    final int PICK_KK = 102;

    final String BASE_URL = "http://10.0.2.2/API_Rental_Mulia/"; // sesuaikan IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, 1);

        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etTelp = findViewById(R.id.etTelp);
        etAlamat = findViewById(R.id.etAlamat);
        ivPreviewKtp = findViewById(R.id.ivPreviewKtp);
        ivPreviewKk = findViewById(R.id.ivPreviewKk);
        layoutUploadKtp = findViewById(R.id.layoutUploadKtp);
        layoutUploadKk = findViewById(R.id.layoutUploadKk);
        etSubmit = findViewById(R.id.etSubmit);

        layoutUploadKtp.setOnClickListener(v -> showImagePickerDialog(PICK_KTP));
        layoutUploadKk.setOnClickListener(v -> showImagePickerDialog(PICK_KK));

        etSubmit.setOnClickListener(v -> {
            if (validate()) {
                try {
                    prosesRegisterGabung();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showImagePickerDialog(int requestCode) {
        String[] options = {"Kamera", "Galeri"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Sumber Gambar")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera(requestCode);
                    else openGallery(requestCode);
                }).show();
    }

    private void openGallery(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, requestCode);
    }

    private void openCamera(int requestCode) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(cameraIntent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = (data != null && data.getData() != null) ? data.getData() : cameraUri;
            if (requestCode == PICK_KTP) {
                uriKtp = selectedImage;
                ivPreviewKtp.setImageURI(uriKtp);
                ivPreviewKtp.setVisibility(View.VISIBLE);
            } else if (requestCode == PICK_KK) {
                uriKk = selectedImage;
                ivPreviewKk.setImageURI(uriKk);
                ivPreviewKk.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean validate() {
        String email = etEmail.getText().toString();
        String telp = etTelp.getText().toString();

        if (etNama.getText().toString().isEmpty() || email.isEmpty() ||
                etPassword.getText().toString().isEmpty() || telp.isEmpty() ||
                etAlamat.getText().toString().isEmpty() || uriKtp == null || uriKk == null) {
            Toast.makeText(this, "Semua data wajib diisi", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            return false;
        }

        if (!telp.matches("^[0-9]{10,13}$")) {
            etTelp.setError("Nomor HP tidak valid");
            return false;
        }

        return true;
    }

    private void prosesRegisterGabung() throws IOException {
        File fileKtp = compressImage(uriKtp, "ktp.jpg");
        File fileKk = compressImage(uriKk, "kk.jpg");

        if (fileKtp.length() > 2 * 1024 * 1024 || fileKk.length() > 2 * 1024 * 1024) {
            Toast.makeText(this, "Ukuran file maksimal 2MB", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody nama     = RequestBody.create(etNama.getText().toString(), MediaType.parse("text/plain"));
        RequestBody email    = RequestBody.create(etEmail.getText().toString(), MediaType.parse("text/plain"));
        RequestBody password = RequestBody.create(etPassword.getText().toString(), MediaType.parse("text/plain"));
        RequestBody telp     = RequestBody.create(etTelp.getText().toString(), MediaType.parse("text/plain"));
        RequestBody alamat   = RequestBody.create(etAlamat.getText().toString(), MediaType.parse("text/plain"));

        MultipartBody.Part partKtp = MultipartBody.Part.createFormData("ktp", fileKtp.getName(),
                RequestBody.create(fileKtp, MediaType.parse("image/*")));
        MultipartBody.Part partKk = MultipartBody.Part.createFormData("kk", fileKk.getName(),
                RequestBody.create(fileKk, MediaType.parse("image/*")));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerAPI api = retrofit.create(ServerAPI.class);
        api.registerWithFile(nama, email, password, telp, alamat, partKtp, partKk)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String res = response.body().string();
                            Log.e("SERVER_RESPONSE", res); // <-- Tambahkan ini!

                            JSONObject obj = new JSONObject(res);
                            if (obj.getInt("result") == 1) {
                                Toast.makeText(Register.this, "Registrasi berhasil", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(Register.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Register.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(Register.this, "Koneksi gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private File compressImage(Uri uri, String filename) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        File file = new File(getCacheDir(), filename);
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
        fos.flush();
        fos.close();
        return file;
    }
}
