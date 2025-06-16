package com.example.rentalmobilmulia.ui.rentalmobil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.databinding.FragmentDetailMobilBinding;
import com.example.rentalmobilmulia.model.MobilDetailResponse;
import com.example.rentalmobilmulia.model.MobilModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailMobilFragment extends Fragment {

    private FragmentDetailMobilBinding binding;
    private MobilModel mobil;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetailMobilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mobil = (MobilModel) getArguments().getSerializable("mobil");
            if (mobil != null) {
                tampilkanDetailMobil();
            } else {
                Toast.makeText(getContext(), "Data mobil tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void tampilkanDetailMobil() {
        if (mobil == null) return;

        String imageUrl = RetrofitClient.BASE_URL + "uploads/" + mobil.getImage1();

        if (mobil.getImage1() != null && !mobil.getImage1().isEmpty()) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.bg_placeholder)
                    .error(R.drawable.bg_placeholder)
                    .into(binding.imageDetailMobil);
        } else {
            binding.imageDetailMobil.setImageResource(R.drawable.bg_placeholder);
        }

        binding.tvNamaMobilDetail.setText(mobil.getNama_mobil());
        binding.tvTransmisiDetail.setText("Transmisi: " + mobil.getTransmisi());
        binding.tvTahunDetail.setText("Tahun: " + mobil.getTahun());
        binding.tvKapasitasDetail.setText("Kapasitas: " + mobil.getSeating());
        binding.tvStatusDetail.setText(mobil.getStatus());
        binding.tvHargaSewaDetail.setText("Rp " + mobil.getHarga_sewa() + " / hari");
        binding.tvDeskripsiDetail.setText(mobil.getDeskripsi());

        if (mobil.getNopol() != null) {
            binding.tvNopolDetail.setText("No. Polisi: " + mobil.getNopol());
        }
        if (mobil.getWarna() != null) {
            binding.tvWarnaDetail.setText("Warna: " + mobil.getWarna());
        }

        binding.btnSewaMobil.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("mobil", mobil);


            SewaMobilFragment sewaFragment = new SewaMobilFragment();
            sewaFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_activity_main, sewaFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
