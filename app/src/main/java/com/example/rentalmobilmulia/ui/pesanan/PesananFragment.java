package com.example.rentalmobilmulia.ui.pesanan;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.model.PesananModel;
import com.example.rentalmobilmulia.model.ResponsePesanan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananFragment extends Fragment {

    private RecyclerView rvPesanan;
    private LinearLayout layoutKosong;
    private EditText etCari;
    private Button btnAktif, btnRiwayat;
    private List<PesananModel> semuaPesanan = new ArrayList<>();
    private PesananAdapter adapter;

    private String email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pesanan, container, false);

        rvPesanan = view.findViewById(R.id.rvPesanan);
        layoutKosong = view.findViewById(R.id.layoutKosong);
        etCari = view.findViewById(R.id.etCariPesanan);
        btnAktif = view.findViewById(R.id.btnAktif);
        btnRiwayat = view.findViewById(R.id.btnRiwayat);

        rvPesanan.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences pref = getActivity().getSharedPreferences("login_pref", getContext().MODE_PRIVATE);
        email = pref.getString("email", "");

        // Listener tombol tab
        btnAktif.setOnClickListener(v -> {
            tampilkanPesanan("aktif");
            updateTabTampilan(true);
        });

        btnRiwayat.setOnClickListener(v -> {
            tampilkanPesanan("riwayat");
            updateTabTampilan(false);
        });

        // Pencarian pesanan
        etCari.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPesanan(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        loadPesanan();
        updateTabTampilan(true); // Default tab: AKTIF

        return view;
    }

    private void loadPesanan() {
        ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
        Call<ResponsePesanan> call = api.getPesananByEmail(email);

        call.enqueue(new Callback<ResponsePesanan>() {
            @Override
            public void onResponse(Call<ResponsePesanan> call, Response<ResponsePesanan> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    semuaPesanan = response.body().getPesanan();
                    tampilkanPesanan("aktif");
                } else {
                    rvPesanan.setVisibility(View.GONE);
                    layoutKosong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ResponsePesanan> call, Throwable t) {
                Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                rvPesanan.setVisibility(View.GONE);
                layoutKosong.setVisibility(View.VISIBLE);
            }
        });
    }

    private void tampilkanPesanan(String tipe) {
        List<PesananModel> filterList = new ArrayList<>();

        for (PesananModel p : semuaPesanan) {
            if (tipe.equals("aktif") &&
                    (p.getStatus().equalsIgnoreCase("Menunggu Pembayaran") ||
                            p.getStatus().equalsIgnoreCase("Sudah Dibayar"))) {
                filterList.add(p);
            } else if (tipe.equals("riwayat") &&
                    (p.getStatus().equalsIgnoreCase("Selesai") ||
                            p.getStatus().equalsIgnoreCase("Dibatalkan"))) {
                filterList.add(p);
            }
        }

        if (filterList.isEmpty()) {
            rvPesanan.setVisibility(View.GONE);
            layoutKosong.setVisibility(View.VISIBLE);
        } else {
            layoutKosong.setVisibility(View.GONE);
            rvPesanan.setVisibility(View.VISIBLE);
            adapter = new PesananAdapter(getContext(), filterList);
            rvPesanan.setAdapter(adapter);
        }
    }

    private void filterPesanan(String keyword) {
        List<PesananModel> hasilFilter = new ArrayList<>();
        for (PesananModel p : semuaPesanan) {
            if (p.getNama_mobil().toLowerCase().contains(keyword.toLowerCase()) ||
                    p.getKode_booking().toLowerCase().contains(keyword.toLowerCase())) {
                hasilFilter.add(p);
            }
        }

        if (adapter != null) {
            adapter = new PesananAdapter(getContext(), hasilFilter);
            rvPesanan.setAdapter(adapter);
        }
    }

    private void updateTabTampilan(boolean isAktifTab) {
        if (getContext() == null) return;

        if (isAktifTab) {
            // AKTIF nyala (merah)
            btnAktif.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.red_primary));
            btnAktif.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

            // RIWAYAT mati (putih)
            btnRiwayat.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), android.R.color.white));
            btnRiwayat.setTextColor(ContextCompat.getColor(getContext(), R.color.red_primary));
        } else {
            // AKTIF mati (putih)
            btnAktif.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), android.R.color.white));
            btnAktif.setTextColor(ContextCompat.getColor(getContext(), R.color.red_primary));

            // RIWAYAT nyala (merah)
            btnRiwayat.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.red_primary));
            btnRiwayat.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        }
    }


}
