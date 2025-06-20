// RiwayatSewaFragment.java (UPDATE: pilih foto & ambil gambar)
package com.example.rentalmobilmulia.ui.profil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.databinding.FragmentRiwayatSewaBinding;
import com.example.rentalmobilmulia.model.ResponseSewa;
import com.example.rentalmobilmulia.model.SewaModel;
import com.example.rentalmobilmulia.model.SewaResponse;
import com.example.rentalmobilmulia.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RiwayatSewaFragment extends Fragment {

    private FragmentRiwayatSewaBinding binding;
    private static final int PICK_IMAGE_REQUEST = 101;
    private static final int CAMERA_REQUEST = 102;
    private SewaModel selectedSewa;
    private Uri imageUri;
    private File imageFile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRiwayatSewaBinding.inflate(inflater, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email tidak ditemukan.", Toast.LENGTH_SHORT).show();
            return binding.getRoot();
        }

        binding.rvRiwayatSewa.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance().getRiwayatSewa(email).enqueue(new Callback<SewaResponse>() {
            @Override
            public void onResponse(@NonNull Call<SewaResponse> call, @NonNull Response<SewaResponse> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    List<SewaModel> sewaList = response.body().getSewa();
                    RiwayatSewaAdapter adapter = new RiwayatSewaAdapter(getContext(), sewaList, sewa -> {
                        selectedSewa = sewa;
                        showImagePicker();
                    });
                    binding.rvRiwayatSewa.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SewaResponse> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }

    private void showImagePicker() {
        String[] options = {"Pilih dari Galeri", "Ambil Foto"};
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Upload Bukti Pembayaran")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, PICK_IMAGE_REQUEST);
                    } else {
                        dispatchTakePictureIntent();
                    }
                })
                .show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            try {
                imageFile = createImageFile();
                imageUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            } catch (IOException ex) {
                Toast.makeText(getContext(), "Gagal membuat file gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == PICK_IMAGE_REQUEST || requestCode == CAMERA_REQUEST) && resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
                imageFile = FileUtils.getFile(requireContext(), imageUri);
            }
            if (imageFile != null && selectedSewa != null) {
                RequestBody kode = RequestBody.create(MediaType.parse("text/plain"), selectedSewa.getKodeBooking());
                RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
                MultipartBody.Part part = MultipartBody.Part.createFormData("bukti_bayar", imageFile.getName(), imageBody);

                RetrofitClient.getInstance().uploadBuktiTransfer(kode, part).enqueue(new Callback<ResponseSewa>() {
                    @Override
                    public void onResponse(Call<ResponseSewa> call, Response<ResponseSewa> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                            Toast.makeText(getContext(), "Upload berhasil", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Gagal upload", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseSewa> call, Throwable t) {
                        Toast.makeText(getContext(), "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}