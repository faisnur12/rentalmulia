package com.example.rentalmobilmulia.ui.rentalmobil;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.model.MobilModel;
import com.example.rentalmobilmulia.model.MobilResponse;
import com.example.rentalmobilmulia.model.ResponseDefault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentCarFragment extends Fragment {

    private RecyclerView recyclerView, categoryRecyclerView;
    private MobilAdapter mobilAdapter;
    private final List<MobilModel> fullMobilList = new ArrayList<>();
    private final List<MobilModel> filteredList = new ArrayList<>();
    private String selectedCategory = "Semua";

    public RentCarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rentalmobil, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        categoryRecyclerView = view.findViewById(R.id.category);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        setupAdapter();
        updateStatusMobilDariServer(); // refresh + update status

        return view;
    }

    // ✅ Ditambahkan agar saat balik ke fragment, status mobil otomatis update
    @Override
    public void onResume() {
        super.onResume();
        updateStatusMobilDariServer();
    }

    private void setupAdapter() {
        mobilAdapter = new MobilAdapter(getContext(), filteredList, new MobilAdapterCallback() {
            @Override
            public void onClickDetail(MobilModel mobil) {
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
            public void onClickSewa(MobilModel mobil) {
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

        recyclerView.setAdapter(mobilAdapter);
    }

    private void loadMobil() {
        ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
        Call<MobilResponse> call = api.getListMobil();

        call.enqueue(new Callback<MobilResponse>() {
            @Override
            public void onResponse(@NonNull Call<MobilResponse> call, @NonNull Response<MobilResponse> response) {
                if (response.body() != null) {
                    Log.e("DEBUG_JSON", new com.google.gson.Gson().toJson(response.body()));
                }

                if (response.isSuccessful() && response.body() != null && response.body().getMobil() != null) {
                    fullMobilList.clear();
                    fullMobilList.addAll(response.body().getMobil());

                    setupKategori(fullMobilList);
                    filterMobil();
                } else {
                    Toast.makeText(getContext(), "Gagal memuat data mobil", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MobilResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Panggil update status dari server sebelum load data mobil
    private void updateStatusMobilDariServer() {
        ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
        Call<ResponseDefault> call = api.updateStatusMobil(); // Pastikan sudah ditambahkan di ServerAPI.java

        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(@NonNull Call<ResponseDefault> call, @NonNull Response<ResponseDefault> response) {
                loadMobil(); // tetap panggil load mobil walau response kosong
            }

            @Override
            public void onFailure(@NonNull Call<ResponseDefault> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Gagal update status mobil", Toast.LENGTH_SHORT).show();
                loadMobil(); // tetap panggil agar data tampil
            }
        });
    }

    private void setupKategori(List<MobilModel> mobilList) {
        Set<String> uniqueMerk = new HashSet<>();
        for (MobilModel mobil : mobilList) {
            if (mobil.getMerk() != null && !mobil.getMerk().isEmpty()) {
                uniqueMerk.add(mobil.getMerk());
            }
        }

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category("Semua"));
        for (String merk : uniqueMerk) {
            categoryList.add(new Category(merk));
        }

        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList, category -> {
            selectedCategory = category;
            filterMobil();
        });

        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    private void filterMobil() {
        filteredList.clear();

        for (MobilModel mobil : fullMobilList) {
            if (selectedCategory.equals("Semua") || mobil.getMerk().equalsIgnoreCase(selectedCategory)) {
                filteredList.add(mobil);
            }
        }

        mobilAdapter.notifyDataSetChanged();
    }
}
