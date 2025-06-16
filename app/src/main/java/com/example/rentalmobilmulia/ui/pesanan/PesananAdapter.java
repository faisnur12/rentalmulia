package com.example.rentalmobilmulia.ui.pesanan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.model.PesananModel;
import com.example.rentalmobilmulia.utils.RupiahFormatter;

import java.util.List;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.ViewHolder> {

    private Context context;
    private List<PesananModel> pesananList;

    public PesananAdapter(Context context, List<PesananModel> pesananList) {
        this.context = context;
        this.pesananList = pesananList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PesananModel pesanan = pesananList.get(position);

        holder.tvNamaMobil.setText(pesanan.getNama_mobil());
        holder.tvTanggal.setText(pesanan.getTanggal_mulai() + " s.d " + pesanan.getTanggal_selesai());
        holder.tvStatus.setText(pesanan.getStatus());
        holder.tvTotal.setText(RupiahFormatter.format(pesanan.getTotal_harga()));

        Glide.with(context)
                .load("http://10.0.2.2/API_Rental_Mulia/uploads/" + pesanan.getFoto_mobil())
                .placeholder(R.drawable.sample_mobil)
                .into(holder.imgMobil);
    }

    @Override
    public int getItemCount() {
        return pesananList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMobil;
        TextView tvNamaMobil, tvTanggal, tvStatus, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMobil = itemView.findViewById(R.id.imgMobil);
            tvNamaMobil = itemView.findViewById(R.id.tvNamaMobil);
            tvTanggal = itemView.findViewById(R.id.tvTanggalSewa);
            tvStatus = itemView.findViewById(R.id.tvStatusPesanan);
            tvTotal = itemView.findViewById(R.id.tvTotalBayar);
        }
    }
}
