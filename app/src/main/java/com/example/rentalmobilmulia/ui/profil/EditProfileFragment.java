package com.example.rentalmobilmulia.ui.profil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import org.json.JSONObject;

import java.io.File;

import id.zelory.compressor.Compressor;
import okhttp3.*;
import retrofit2.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int CAMERA_REQUEST = 1002;

    private EditText  etEmail, etNama, etAlamat, etTelp;
    private Button btnSubmit, btnBack;
    private ShapeableImageView imageViewProfile;
    private FloatingActionButton btnUploadPhoto;
    private Uri imageUri;
    private String email;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        etEmail = root.findViewById(R.id.etProfile_email);
        etNama = root.findViewById(R.id.etProfile_Nama);
        etAlamat = root.findViewById(R.id.etProfile_alamat);
        etTelp = root.findViewById(R.id.etProfile_telp);
        btnSubmit = root.findViewById(R.id.tvProfileSubmit);
        btnBack = root.findViewById(R.id.btnBack);
        imageViewProfile = root.findViewById(R.id.img_profile);
        btnUploadPhoto = root.findViewById(R.id.btn_upload_photo);

        sharedPreferences = requireContext().getSharedPreferences("login_pref", 0);
        editor = sharedPreferences.edit();
        email = sharedPreferences.getString("email", "");

        etEmail.setText(email);          // Tampilkan email
        etEmail.setEnabled(false);       // Disable agar tidak bisa diedit

        // Tampilkan gambar profil dari SharedPreferences jika ada
        String imageName = sharedPreferences.getString("profile_image", "");
        if (!imageName.isEmpty()) {
            String imageUrl = RetrofitClient.BASE_URL + "uploads/" + imageName;
            Glide.with(this).load(imageUrl).into(imageViewProfile);
        }

        if (!email.isEmpty()) {
            loadProfile(email);
        }

        btnSubmit.setOnClickListener(v -> {
            if (etNama.getText().toString().trim().isEmpty()) {
                etNama.setError("Nama tidak boleh kosong");
                etNama.requestFocus();
                return;
            }

            updateProfile(new DataPelanggan(
                    email,
                    etNama.getText().toString().trim(),
                    etAlamat.getText().toString().trim(),
                    etTelp.getText().toString().trim()
            ));
        });

        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        btnUploadPhoto.setOnClickListener(v -> showImagePickerDialog());

        return root;
    }

    private void showImagePickerDialog() {
        String[] opsi = {"Pilih dari Galeri", "Ambil dari Kamera"};
        new AlertDialog.Builder(requireContext())
                .setTitle("Unggah Foto Profil")
                .setItems(opsi, (dialog, i) -> {
                    if (i == 0) openFileChooser();
                    else openCamera();
                })
                .show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Foto Baru");
        imageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, @Nullable Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (resCode == getActivity().RESULT_OK) {
            if (reqCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
            }
            if (imageUri != null) {
                Glide.with(requireContext())
                        .load(imageUri)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.foto_profile)
                        .into(imageViewProfile);

                uploadImage(imageUri); // PENTING: upload gambar setelah dipilih
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getContext(), uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            int idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(idx);
            cursor.close();
            return path;
        }
        return null;
    }

    private void uploadImage(Uri uri) {
        String path = getRealPathFromURI(uri);
        if (path == null) {
            Toast.makeText(getContext(), "Gagal mengambil path gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File original = new File(path);
            File compressed = new Compressor(requireContext())
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .compressToFile(original);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), compressed);
            MultipartBody.Part body = MultipartBody.Part.createFormData("imageupload", compressed.getName(), requestFile);
            RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);

            ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
            api.uploadImage(body, emailBody).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> res) {
                    try {
                        if (res.body() != null) {
                            JSONObject obj = new JSONObject(res.body().string());
                            String imageName = obj.optString("filename", "");

                            Toast.makeText(getContext(), obj.getString("pesan"), Toast.LENGTH_SHORT).show();

                            if (!imageName.isEmpty()) {
                                editor.putString("profile_image", imageName);
                                editor.apply();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("UploadImage", e.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "Upload gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Gagal kompres", Toast.LENGTH_SHORT).show();
            Log.e("Compressor", e.getMessage());
        }
    }

    private void loadProfile(String vemail) {
        ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
        api.getProfile(vemail).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> res) {
                try {
                    if (res.body() != null) {
                        JSONObject obj = new JSONObject(res.body().string());
                        if (obj.getInt("result") == 1) {
                            JSONObject data = obj.getJSONObject("data");

                            etNama.setText(data.getString("nama"));
                            etAlamat.setText(data.getString("alamat"));
                            etTelp.setText(data.getString("telp"));
                        } else {
                            Toast.makeText(getContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e("LoadProfile", e.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal koneksi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile(DataPelanggan data) {
        ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
        api.updateProfile(data.getNama_user(), data.getAlamat(), data.getTelp(), data.getEmail())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> res) {
                        try {
                            if (res.body() != null) {
                                JSONObject obj = new JSONObject(res.body().string());
                                Toast.makeText(getContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                editor.putString("nama", data.getNama_user()); // SESUAI permintaan
                                editor.apply();
                            }
                        } catch (Exception e) {
                            Log.e("UpdateProfile", e.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Gagal update profil", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
