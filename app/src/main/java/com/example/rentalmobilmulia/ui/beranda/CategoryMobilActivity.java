package com.example.rentalmobilmulia.ui.beranda;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.model.MobilModel;
import com.example.rentalmobilmulia.model.MobilResponse;
import com.example.rentalmobilmulia.ui.rentalmobil.DetailMobilFragment;
import com.example.rentalmobilmulia.ui.rentalmobil.SewaMobilFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryMobilActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BerandaMobilAdapter adapter;
    private String kategori;

    private static final String TAG = "CategoryMobilActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_mobil);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        kategori = getIntent().getStringExtra("kategori");
        if (kategori == null || kategori.trim().isEmpty()) {
            Toast.makeText(this, "Kategori tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadMobilByKategori(kategori);
    }

    private void loadMobilByKategori(String kategori) {
        ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
        Call<MobilResponse> call = api.getListMobil();

        call.enqueue(new Callback<MobilResponse>() {
            @Override
            public void onResponse(Call<MobilResponse> call, Response<MobilResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(CategoryMobilActivity.this, "Gagal mengambil data dari server", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Response gagal: " + response.code());
                    return;
                }

                MobilResponse mobilResponse = response.body();
                if (mobilResponse.getResult() != 1) {
                    Toast.makeText(CategoryMobilActivity.this, "Data tidak tersedia", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<MobilModel> allMobil = mobilResponse.getMobil();
                List<MobilModel> filteredList = new ArrayList<>();

                for (MobilModel mobil : allMobil) {
                    if (mobil.getMerk() != null &&
                            mobil.getMerk().trim().equalsIgnoreCase(kategori.trim())) {
                        filteredList.add(mobil);
                    }
                }

                if (filteredList.isEmpty()) {
                    Toast.makeText(CategoryMobilActivity.this, "Tidak ada mobil dengan kategori: " + kategori, Toast.LENGTH_SHORT).show();
                }

                // Pasang adapter dengan listener
                adapter = new BerandaMobilAdapter(CategoryMobilActivity.this, filteredList, new BerandaMobilAdapter.OnMobilClickListener() {
                    @Override
                    public void onDetailClick(MobilModel mobil) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("mobil", mobil);

                        DetailMobilFragment fragment = new DetailMobilFragment();
                        fragment.setArguments(bundle);

                        getSupportFragmentManager()
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

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.nav_host_fragment_activity_main, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<MobilResponse> call, Throwable t) {
                Toast.makeText(CategoryMobilActivity.this, "Kesalahan jaringan: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }
}
