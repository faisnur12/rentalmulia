package com.example.rentalmobilmulia.ui.rentalmobil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.model.MobilModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MobilAdapter extends RecyclerView.Adapter<MobilAdapter.ViewHolder> {

    private final Context context;
    private final List<MobilModel> list;
    private final MobilAdapterCallback callback;

    public MobilAdapter(Context context, List<MobilModel> list, MobilAdapterCallback callback) {
        this.context = context;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MobilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_rental_mobil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MobilAdapter.ViewHolder holder, int position) {
        MobilModel mobil = list.get(position);

        holder.tvNama.setText(mobil.getNama_mobil());
        holder.tvTransmisi.setText("Transmisi: " + (mobil.getTransmisi() != null ? mobil.getTransmisi() : "-"));
        holder.tvHarga.setText("Rp" + String.format("%,.0f", mobil.getHarga_sewa()) + " / hari");
        holder.tvTotalDisewa.setText("Disewa: " + mobil.getTotal_disewa() + "x");

        // Gambar
        try {
            String imageName = mobil.getImage1() != null ? mobil.getImage1() : "";
            String encodedFilename = URLEncoder.encode(imageName, "UTF-8").replace("+", "%20");
            String url = "http://10.0.2.2/API_Rental_Mulia/uploads/" + encodedFilename;

            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.bg_placeholder)
                    .error(R.drawable.bg_placeholder)
                    .into(holder.imageMobil);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Status dan warna
        String status = mobil.getStatus() != null ? mobil.getStatus().trim() : "-";
        holder.tvStatus.setText(status);

        int statusColor;
        if (status.equalsIgnoreCase("tersedia")) {
            statusColor = ContextCompat.getColor(context, R.color.green);
        } else if (status.equalsIgnoreCase("disewa")) {
            statusColor = ContextCompat.getColor(context, R.color.red_primary);
        } else {
            statusColor = ContextCompat.getColor(context, R.color.gray_dark);
        }
        holder.tvStatus.setTextColor(statusColor);

        // Tombol sewa
        boolean isAvailable = "tersedia".equalsIgnoreCase(status);
        // Biarkan tombol selalu aktif
        holder.btnSewa.setEnabled(true);
        holder.btnSewa.setAlpha(1f);


        holder.btnDetail.setOnClickListener(v -> {
            if (callback != null) callback.onClickDetail(mobil);
        });

        holder.btnSewa.setOnClickListener(v -> {
            if (callback != null) callback.onClickSewa(mobil);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvTransmisi, tvHarga, tvStatus, tvTotalDisewa;
        ImageView imageMobil;
        Button btnSewa;
        ImageButton btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaMobil);
            tvTransmisi = itemView.findViewById(R.id.tvTransmisi);
            tvHarga = itemView.findViewById(R.id.tvHargaSewa);
            tvStatus = itemView.findViewById(R.id.tvStatusMobil);
            tvTotalDisewa = itemView.findViewById(R.id.tvTotalDisewa);
            imageMobil = itemView.findViewById(R.id.imageMobil);
            btnSewa = itemView.findViewById(R.id.btnSewa);
            btnDetail = itemView.findViewById(R.id.btnDetailRental);
        }
    }
}
