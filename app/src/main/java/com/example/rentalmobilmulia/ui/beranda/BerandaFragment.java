package com.example.rentalmobilmulia.ui.beranda;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.databinding.FragmentBerandaBinding;
import com.example.rentalmobilmulia.model.MobilModel;
import com.example.rentalmobilmulia.model.MobilResponse;
import com.example.rentalmobilmulia.ui.beranda.ChatFragment;
import com.example.rentalmobilmulia.ui.rentalmobil.*;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BerandaFragment extends Fragment {

    private FragmentBerandaBinding binding;
    private final List<MobilModel> mobilList = new ArrayList<>();
    private MobilAdapter mobilAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBerandaBinding.inflate(inflater, container, false);

        setupSlider();
        setupKategori();
        setupRecyclerMobil();
        setupSwipeToRefresh();
        setupLihatSemuaAction();
        setupLiveChatButton();
        showLocalUserData();

        // Load data pertama kali
        loadSemuaMobil();

        return binding.getRoot();
    }

    private void setupSlider() {
        List<SlideModel> slideModels = Arrays.asList(
                new SlideModel(R.drawable.banner1, ScaleTypes.FIT),
                new SlideModel(R.drawable.banner2, ScaleTypes.FIT),
                new SlideModel(R.drawable.banner3, ScaleTypes.FIT)
        );
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }

    private void setupKategori() {
        List<CategoryBeranda> kategoriList = Arrays.asList(
                new CategoryBeranda(R.drawable.toyota, "Toyota", "15"),
                new CategoryBeranda(R.drawable.hyundai, "Hyundai", "16"),
                new CategoryBeranda(R.drawable.honda, "Honda", "37")
        );
        CategoryBerandaAdapter categoryAdapter = new CategoryBerandaAdapter(kategoriList, category -> {
            Toast.makeText(getContext(), "Kategori: " + category.getNama(), Toast.LENGTH_SHORT).show();
        });
        binding.rvCategory.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.rvCategory.setAdapter(categoryAdapter);
    }

    private void setupRecyclerMobil() {
        mobilAdapter = new MobilAdapter(getContext(), mobilList, new MobilAdapterCallback() {
            @Override public void onClickDetail(MobilModel mobil) {
                navigateToDetail(mobil);
            }
            @Override public void onClickSewa(MobilModel mobil) {
                navigateToSewa(mobil);
            }
        });
        binding.rvRekomendasiProduk.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        binding.rvRekomendasiProduk.setAdapter(mobilAdapter);
    }

    private void setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadSemuaMobil);
    }

    private void setupLihatSemuaAction() {
        binding.btnLihatSemuaMobil.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Tombol 'Lihat Semua' ditekan", Toast.LENGTH_SHORT).show();
        });
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

    private void loadSemuaMobil() {
        binding.progressLoadingMobil.setVisibility(View.VISIBLE);
        ServerAPI api = RetrofitClient.getInstance();
        api.getListMobil().enqueue(new Callback<MobilResponse>() {
            @Override
            public void onResponse(@NonNull Call<MobilResponse> call, @NonNull Response<MobilResponse> response) {
                binding.swipeRefresh.setRefreshing(false);
                binding.progressLoadingMobil.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getMobil() != null) {
                    mobilList.clear();
                    mobilList.addAll(response.body().getMobil());
                    mobilAdapter.notifyDataSetChanged();
                    Log.d("BERANDA_MOBIL", "Total mobil dikembalikan: " + mobilList.size());
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data mobil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MobilResponse> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                binding.progressLoadingMobil.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDetail(MobilModel mobil) {
        DetailMobilFragment fragment = new DetailMobilFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("mobil", mobil);
        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToSewa(MobilModel mobil) {
        SewaMobilFragment fragment = new SewaMobilFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("mobil", mobil);
        fragment.setArguments(bundle);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showLocalUserData() {
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("login_pref", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        String nama = sharedPreferences.getString("nama", "Guest");
        String profileImage = sharedPreferences.getString("profile_image", "");

        binding.tvUsername.setText("Hai, " + (isLoggedIn ? nama : "Guest"));
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        showLocalUserData();
    }
}
