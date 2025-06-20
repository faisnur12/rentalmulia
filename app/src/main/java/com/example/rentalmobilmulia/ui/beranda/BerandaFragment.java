package com.example.rentalmobilmulia.ui.beranda;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.databinding.FragmentBerandaBinding;
import com.example.rentalmobilmulia.model.MobilModel;
import com.example.rentalmobilmulia.ui.rentalmobil.DetailMobilFragment;
import com.example.rentalmobilmulia.ui.rentalmobil.SewaMobilFragment;
import com.example.rentalmobilmulia.ui.beranda.ChatFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BerandaFragment extends Fragment {

    private FragmentBerandaBinding binding;
    private final List<MobilModel> rekomendasiMobilList = new ArrayList<>();
    private BerandaMobilAdapter mobilAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBerandaBinding.inflate(inflater, container, false);

        setupSlider();
        setupKategori();
        setupRecyclerMobil();
        showLocalUserData();
        setupLiveChatButton();
        loadRekomendasiMobil();

        return binding.getRoot();
    }

    private void setupLiveChatButton() {
        binding.btnLiveChat.setOnClickListener(v -> {
            ChatFragment chatFragment = new ChatFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, chatFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }


    private void setupSlider() {
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void setupKategori() {
        List<CategoryBeranda> kategoriList = new ArrayList<>();
        kategoriList.add(new CategoryBeranda(R.drawable.toyota, "Toyota", "15"));
        kategoriList.add(new CategoryBeranda(R.drawable.hyundai, "Hyundai", "16"));
        kategoriList.add(new CategoryBeranda(R.drawable.honda, "Honda", "37"));

        CategoryBerandaAdapter categoryAdapter = new CategoryBerandaAdapter(kategoriList, category -> {
            Intent intent = new Intent(getContext(), CategoryMobilActivity.class);
            intent.putExtra("kategori", category.getNama());
            startActivity(intent);
        });

        binding.rvCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategory.setAdapter(categoryAdapter);
    }

    private void setupRecyclerMobil() {
        mobilAdapter = new BerandaMobilAdapter(getContext(), rekomendasiMobilList, new BerandaMobilAdapter.OnMobilClickListener() {
            @Override
            public void onDetailClick(MobilModel mobil) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("mobil", mobil);

                DetailMobilFragment fragment = new DetailMobilFragment();
                fragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onSewaClick(MobilModel mobil) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("mobil", mobil);

                SewaMobilFragment fragment = new SewaMobilFragment();
                fragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_main, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        binding.rvRekomendasiMobil.setLayoutManager(new GridLayoutManager(getContext(), 1));
        binding.rvRekomendasiMobil.setAdapter(mobilAdapter);
    }

    private void loadRekomendasiMobil() {
        ServerAPI api = RetrofitClient.getInstance();
        Call<List<MobilModel>> call = api.getRekomendasiMobil(); // Sudah sorted LIMIT 3

        call.enqueue(new Callback<List<MobilModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MobilModel>> call, @NonNull Response<List<MobilModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rekomendasiMobilList.clear();
                    rekomendasiMobilList.addAll(response.body());
                    mobilAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Gagal memuat mobil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MobilModel>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLocalUserData() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        String nama = sharedPreferences.getString("nama", "Guest");
        String profileImage = sharedPreferences.getString("profile_image", "");

        binding.tvUsername.setText("Hai, " + nama);

        if (!profileImage.isEmpty()) {
            String imageUrl = RetrofitClient.BASE_URL_IMAGE + profileImage;
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.foto_profile)
                    .into(binding.imgProfile);
        } else {
            binding.imgProfile.setImageResource(R.drawable.foto_profile);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        showLocalUserData(); // refresh info user
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
