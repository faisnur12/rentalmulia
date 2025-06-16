package com.example.rentalmobilmulia.ui.rentalmobil;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.KonfirmasiSewaActivity;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.model.KetersediaanResponse;
import com.example.rentalmobilmulia.model.MobilModel;
import com.example.rentalmobilmulia.utils.RupiahFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SewaMobilFragment extends Fragment {

    private ImageView imageMobil;
    private TextView tvNamaMobil, tvHargaSewa, tvSeats, tvTahun, tvBahanBakar;
    private EditText etTanggalMulai, etTanggalSelesai;
    private Spinner spinnerMetodePickup;
    private RadioGroup rgDriver;
    private Button btnCekKetersediaan;

    private MobilModel mobilDipilih;
    private ServerAPI apiService;
    private Calendar calendar;

    private static final String BASE_URL_FOTO = "http://10.0.2.2/API_Rental_Mulia/uploads/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sewa_mobil, container, false);

        initView(view);

        apiService = RetrofitClient.getClient().create(ServerAPI.class);
        calendar = Calendar.getInstance();

        if (getArguments() != null) {
            mobilDipilih = (MobilModel) getArguments().getSerializable("mobil");
        }

        if (mobilDipilih == null) {
            Toast.makeText(getContext(), "Data mobil tidak ditemukan", Toast.LENGTH_SHORT).show();
            return view;
        }

        tampilkanDataMobil(mobilDipilih);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.metode_pickup_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMetodePickup.setAdapter(adapter);

        etTanggalMulai.setOnClickListener(v -> showDatePicker(etTanggalMulai));
        etTanggalSelesai.setOnClickListener(v -> showDatePicker(etTanggalSelesai));

        btnCekKetersediaan.setOnClickListener(v -> cekKetersediaan());

        return view;
    }

    private void initView(View view) {
        imageMobil = view.findViewById(R.id.imageMobil);
        tvNamaMobil = view.findViewById(R.id.tvNamaMobil);
        tvHargaSewa = view.findViewById(R.id.tvHargaSewa);
        tvSeats = view.findViewById(R.id.tvSeats);
        tvTahun = view.findViewById(R.id.tvTahun);
        tvBahanBakar = view.findViewById(R.id.tvBahanBakar);
        etTanggalMulai = view.findViewById(R.id.etTanggalMulai);
        etTanggalSelesai = view.findViewById(R.id.etTanggalSelesai);
        spinnerMetodePickup = view.findViewById(R.id.spinnerMetodePickup);
        rgDriver = view.findViewById(R.id.rgDriver);
        btnCekKetersediaan = view.findViewById(R.id.btnCekKetersediaan);
    }

    private void tampilkanDataMobil(MobilModel mobil) {
        tvNamaMobil.setText(mobil.getNama_mobil());
        tvHargaSewa.setText(RupiahFormatter.format(mobil.getHarga_sewa()) + " / Hari");
        tvSeats.setText(mobil.getSeating());
        tvTahun.setText(mobil.getTahun());
        tvBahanBakar.setText(mobil.getBb());

        String fotoUrl = mobil.getImage1();
        if (!fotoUrl.startsWith("http://10.0.2.2/API_Rental_Mulia")) {
            fotoUrl = BASE_URL_FOTO + fotoUrl;
        }

        Glide.with(this)
                .load(fotoUrl)
                .placeholder(R.drawable.sample_mobil)
                .error(R.drawable.sample_mobil)
                .into(imageMobil);
    }

    private void showDatePicker(EditText target) {
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            target.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void cekKetersediaan() {
        String tanggalMulai = etTanggalMulai.getText().toString();
        String tanggalSelesai = etTanggalSelesai.getText().toString();

        if (tanggalMulai.isEmpty() || tanggalSelesai.isEmpty()) {
            Toast.makeText(getContext(), "Harap isi tanggal sewa dan kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validasiTanggal(tanggalMulai, tanggalSelesai)) {
            Toast.makeText(getContext(), "Tanggal selesai harus setelah tanggal mulai", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgDriver.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Pilih opsi driver", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.cekKetersediaan(mobilDipilih.getId_mobil(), tanggalMulai, tanggalSelesai)
                .enqueue(new Callback<KetersediaanResponse>() {
                    @Override
                    public void onResponse(Call<KetersediaanResponse> call, Response<KetersediaanResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isTersedia()) {
                                // Intent ke Konfirmasi
                                Intent intent = new Intent(requireActivity(), KonfirmasiSewaActivity.class);
                                intent.putExtra("id_mobil", mobilDipilih.getId_mobil());
                                intent.putExtra("nama_mobil", mobilDipilih.getNama_mobil());
                                intent.putExtra("harga_sewa", mobilDipilih.getHarga_sewa());
                                intent.putExtra("tanggal_mulai", tanggalMulai);
                                intent.putExtra("tanggal_selesai", tanggalSelesai);
                                intent.putExtra("metode_pickup", spinnerMetodePickup.getSelectedItem().toString());
                                intent.putExtra("foto_mobil", mobilDipilih.getImage1());

                                String driver = rgDriver.getCheckedRadioButtonId() == R.id.rbDriverYa ? "Ya" : "Tidak";
                                intent.putExtra("driver", driver);

                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Mobil tidak tersedia di tanggal tersebut", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Gagal memeriksa ketersediaan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<KetersediaanResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validasiTanggal(String mulai, String selesai) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(selesai).after(sdf.parse(mulai));
        } catch (ParseException e) {
            return false;
        }
    }
}
