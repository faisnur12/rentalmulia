package com.example.rentalmobilmulia.ui.beranda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.model.MobilModel;
import com.example.rentalmobilmulia.utils.RupiahFormatter;

import java.util.List;

public class BerandaMobilAdapter extends RecyclerView.Adapter<BerandaMobilAdapter.ViewHolder> {

    public interface OnMobilClickListener {
        void onDetailClick(MobilModel mobil);
        void onSewaClick(MobilModel mobil);
    }

    private Context context;
    private List<MobilModel> mobilList;
    private OnMobilClickListener listener;

    public BerandaMobilAdapter(Context context, List<MobilModel> mobilList, OnMobilClickListener listener) {
        this.context = context;
        this.mobilList = mobilList;
        this.listener = listener;
    }

    public BerandaMobilAdapter(Context context, List<MobilModel> mobilList) {
        this(context, mobilList, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_rental_mobil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MobilModel mobil = mobilList.get(position);

        holder.tvNamaMobil.setText(mobil.getNama_mobil());
        holder.tvTransmisi.setText("Transmisi: " + mobil.getTransmisi());
        holder.tvHargaSewa.setText(RupiahFormatter.format(mobil.getHarga_sewa()) + " / hari");
        holder.tvTotalDisewa.setText("Disewa: " + mobil.getTotal_disewa() + "x");

        holder.tvStatus.setText(mobil.getStatus());
        if (mobil.getStatus().equalsIgnoreCase("Tersedia")) {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.green));
            holder.btnSewa.setEnabled(true);
        } else {
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.red_primary));
            holder.btnSewa.setEnabled(false);
        }

        String imageUrl = "http://10.0.2.2/API_Rental_Mulia/uploads/" + mobil.getImage1();
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.imageMobil);

        // Tombol Sewa klik
        holder.btnSewa.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSewaClick(mobil);
            }
        });

        // Tombol Detail klik
        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDetailClick(mobil);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mobilList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMobil;
        TextView tvNamaMobil, tvTransmisi, tvHargaSewa, tvStatus, tvTotalDisewa;
        Button btnSewa;
        ImageButton btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMobil = itemView.findViewById(R.id.imageMobil);
            tvNamaMobil = itemView.findViewById(R.id.tvNamaMobil);
            tvTransmisi = itemView.findViewById(R.id.tvTransmisi);
            tvHargaSewa = itemView.findViewById(R.id.tvHargaSewa);
            tvStatus = itemView.findViewById(R.id.tvStatusMobil);
            tvTotalDisewa = itemView.findViewById(R.id.tvTotalDisewa);
            btnSewa = itemView.findViewById(R.id.btnSewa);
            btnDetail = itemView.findViewById(R.id.btnDetailRental);
        }
    }
}
