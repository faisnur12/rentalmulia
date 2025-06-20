package com.example.rentalmobilmulia.ui.profil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilFragment extends Fragment {

    private TextView tvWelcomeLabel, tvUsername;
    private ImageView imgProfile;
    private LinearLayout btnEditProfile,btnRiwayatSewa, btnKontakKami, btnLogout;
    private SharedPreferences sharedPreferences;

    public ProfilFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvWelcomeLabel = view.findViewById(R.id.tvWelcomeLabel);
        tvUsername = view.findViewById(R.id.tvUsername);
        imgProfile = view.findViewById(R.id.img_profile);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnKontakKami = view.findViewById(R.id.btnKontakKami);
        btnLogout = view.findViewById(R.id.btnLogout);

        sharedPreferences = requireActivity().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_loginGuestFragment);
            return;
        }

        // Tampilkan data lokal terlebih dahulu
        showLocalProfile();

        // Ambil data profil dari server (jika ada email)
        String email = sharedPreferences.getString("email", "");
        if (!email.isEmpty()) {
            fetchProfileFromServer(email);
        }

        btnEditProfile.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_editProfileFragment));

        btnRiwayatSewa = view.findViewById(R.id.btnRiwayatSewa);

        btnRiwayatSewa.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_riwayatSewaFragment));


        btnKontakKami.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_contactFragment));

        btnLogout.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Toast.makeText(getActivity(), "Berhasil logout", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigate(R.id.action_navigation_profile_to_loginGuestFragment);
        });
    }

    private void showLocalProfile() {
        String nama = sharedPreferences.getString("nama", "Guest");
        String profileImage = sharedPreferences.getString("profile_image", "");

        tvUsername.setText(nama);
        tvWelcomeLabel.setText("Anda Login Sebagai:");

        if (!profileImage.isEmpty()) {
            String imageUrl = RetrofitClient.BASE_URL_IMAGE + profileImage;
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.foto_profile)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.foto_profile);
        }
    }

    private void fetchProfileFromServer(String email) {
        ServerAPI api = RetrofitClient.getInstance();
        api.getProfile(email).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String res = response.body().string();
                        JSONObject obj = new JSONObject(res);
                        if (obj.getInt("result") == 1) {
                            JSONObject data = obj.getJSONObject("data");
                            String nama = data.getString("nama");
                            String imageName = data.optString("profile_image", "");

                            // Tampilkan UI
                            tvUsername.setText(nama);
                            tvWelcomeLabel.setText("Anda Login Sebagai:");

                            if (!imageName.isEmpty()) {
                                String imageUrl = RetrofitClient.BASE_URL_IMAGE + imageName;
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.profile)
                                        .error(R.drawable.foto_profile)
                                        .into(imgProfile);
                            }

                            // Simpan ke SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("nama", nama); // penting: konsisten dengan key saat login
                            editor.putString("profile_image", imageName);
                            editor.apply();

                        } else {
                            Toast.makeText(getActivity(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e("ProfilFragment", "Parsing error: " + e.getMessage());
                        Toast.makeText(getActivity(), "Gagal parsing data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Gagal memuat data profil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("ProfilFragment", "onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), "Koneksi gagal", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
