package com.example.rentalmobilmulia.ui.pesanan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.model.PesananModel;

import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananFragment extends Fragment {

    private RecyclerView rvPesanan;
    private LinearLayout layoutKosong;
    private EditText etCariPesanan;
    private Button btnAktif, btnRiwayat;
    private PesananAdapter adapter;
    private List<PesananModel> pesananList = new ArrayList<>();
    private List<PesananModel> filteredList = new ArrayList<>();
    private String currentStatus = "Menunggu Konfirmasi";

    private ServerAPI apiService;
    private String idUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesanan, container, false);

        // Inisialisasi
        rvPesanan = view.findViewById(R.id.rvPesanan);
        layoutKosong = view.findViewById(R.id.layoutKosong);
        etCariPesanan = view.findViewById(R.id.etCariPesanan);
        btnAktif = view.findViewById(R.id.btnAktif);
        btnRiwayat = view.findViewById(R.id.btnRiwayat);

        rvPesanan.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PesananAdapter(getContext(), filteredList);
        rvPesanan.setAdapter(adapter);

        apiService = RetrofitClient.getClient().create(ServerAPI.class);
        idUser = getUserId();

        // Listener tombol tab
        btnAktif.setOnClickListener(v -> {
            currentStatus = "Menunggu Konfirmasi";
            loadPesanan();
            setTabActive(true);
        });

        btnRiwayat.setOnClickListener(v -> {
            currentStatus = "Selesai";
            loadPesanan();
            setTabActive(false);
        });

        // Search
        etCariPesanan.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // Load awal
        setTabActive(true);
        loadPesanan();

        return view;
    }

    private void loadPesanan() {
        if (idUser == null || idUser.isEmpty()) {
            Toast.makeText(getContext(), "Pengguna belum login", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getPesanan(idUser, currentStatus).enqueue(new Callback<List<PesananModel>>() {
            @Override
            public void onResponse(Call<List<PesananModel>> call, Response<List<PesananModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pesananList.clear();
                    pesananList.addAll(response.body());
                    filterData(etCariPesanan.getText().toString());
                } else {
                    showKosong();
                }
            }

            @Override
            public void onFailure(Call<List<PesananModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                showKosong();
            }
        });
    }

    private void filterData(String keyword) {
        filteredList.clear();
        if (keyword.isEmpty()) {
            filteredList.addAll(pesananList);
        } else {
            for (PesananModel item : pesananList) {
                if (item.getNama_mobil().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }

        if (filteredList.isEmpty()) {
            showKosong();
        } else {
            layoutKosong.setVisibility(View.GONE);
            rvPesanan.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void showKosong() {
        rvPesanan.setVisibility(View.GONE);
        layoutKosong.setVisibility(View.VISIBLE);
    }

    private void setTabActive(boolean aktif) {
        if (aktif) {
            btnAktif.setBackgroundResource(R.drawable.tab_left_selected);
            btnRiwayat.setBackgroundResource(R.drawable.tab_right_unselected);
        } else {
            btnAktif.setBackgroundResource(R.drawable.tab_left_selected);
            btnRiwayat.setBackgroundResource(R.drawable.tab_right_unselected);
        }
    }

    private String getUserId() {
        SharedPreferences pref = requireContext().getSharedPreferences("user_session", getContext().MODE_PRIVATE);
        return String.valueOf(pref.getInt("id", 0));
    }
}
