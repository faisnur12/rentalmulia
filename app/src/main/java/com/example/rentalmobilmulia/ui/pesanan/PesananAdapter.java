package com.example.rentalmobilmulia.ui.pesanan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.model.PesananModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {

    private Context context;
    private List<PesananModel> list;
    private static final String BASE_URL_IMAGE = "http://10.0.2.2/API_Rental_Mulia/uploads/";

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
        PesananModel item = list.get(position);

        holder.tvNamaMobil.setText(item.getNama_mobil());
        holder.tvTanggal.setText(item.getTgl_mulai() + " s/d " + item.getTgl_selesai());
        holder.tvStatus.setText(item.getStatus());
        holder.tvTotal.setText(formatRupiah(item.getTotal()));

        Glide.with(context)
                .load(BASE_URL_IMAGE + item.getFoto_mobil())
                .placeholder(R.drawable.sample_mobil)
                .into(holder.imgMobil);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailPesananActivity.class);
            intent.putExtra("kode_booking", item.getKode_booking());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMobil;
        TextView tvNamaMobil, tvTanggal, tvStatus, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMobil = itemView.findViewById(R.id.imgMobil);
            tvNamaMobil = itemView.findViewById(R.id.tvNamaMobil);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }

    private String formatRupiah(double number) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatter.format(number);
    }
}