package com.example.rentalmobilmulia.ui.pesanan;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.ServerAPI;
import com.example.rentalmobilmulia.model.PesananModel;
import com.example.rentalmobilmulia.model.ResponseDefault;

import java.text.NumberFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {

    private final Context context;
    private final List<PesananModel> list;
    private static final String BASE_IMAGE_URL = "http://10.0.2.2/API_Rental_Mulia/uploads/";

    public PesananAdapter(Context context, List<PesananModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PesananModel p = list.get(position);

        holder.tvNamaMobil.setText(p.getNama_mobil());
        holder.tvTanggal.setText(p.getTgl_mulai() + " - " + p.getTgl_selesai());
        holder.tvStatus.setText(p.getStatus());
        holder.tvTotal.setText("Total: " + formatRupiah(p.getTotal()));

        Glide.with(context)
                .load(BASE_IMAGE_URL + p.getFoto_mobil())
                .placeholder(R.drawable.sample_mobil)
                .into(holder.imgMobil);

        if (p.getStatus().equalsIgnoreCase("Menunggu Pembayaran")) {
            holder.btnBatalkan.setVisibility(View.VISIBLE);
        } else {
            holder.btnBatalkan.setVisibility(View.GONE);
        }

        holder.btnBatalkan.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Batalkan Pesanan")
                    .setMessage("Yakin ingin membatalkan pesanan ini?")
                    .setPositiveButton("Ya", (dialog, which) -> batalkanPesanan(p.getKode_booking(), position))
                    .setNegativeButton("Tidak", null)
                    .show();
        });
    }

    private void batalkanPesanan(String kode_booking, int position) {
        ServerAPI api = RetrofitClient.getClient().create(ServerAPI.class);
        Call<ResponseDefault> call = api.batalkanSewa(kode_booking);

        call.enqueue(new Callback<ResponseDefault>() {
            @Override
            public void onResponse(Call<ResponseDefault> call, Response<ResponseDefault> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess() == 1) {
                    Toast.makeText(context, "Pesanan dibatalkan", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                } else {
                    Toast.makeText(context, "Gagal membatalkan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseDefault> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatRupiah(double number) {
        return NumberFormat.getCurrencyInstance(new Locale("in", "ID")).format(number);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMobil;
        TextView tvNamaMobil, tvTanggal, tvStatus, tvTotal;
        Button btnBatalkan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMobil = itemView.findViewById(R.id.imgMobil);
            tvNamaMobil = itemView.findViewById(R.id.tvNamaMobil);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnBatalkan = itemView.findViewById(R.id.btnBatalkan);
        }
    }
}
