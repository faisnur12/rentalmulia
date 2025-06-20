package com.example.rentalmobilmulia.ui.beranda;

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
import com.example.rentalmobilmulia.utils.RupiahFormatter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class BerandaMobilAdapter extends RecyclerView.Adapter<BerandaMobilAdapter.ViewHolder> {

    public interface OnMobilClickListener {
        void onDetailClick(MobilModel mobil);
        void onSewaClick(MobilModel mobil);
    }

    private final Context context;
    private final List<MobilModel> mobilList;
    private final OnMobilClickListener listener;

    public BerandaMobilAdapter(Context context, List<MobilModel> mobilList, OnMobilClickListener listener) {
        this.context = context;
        this.mobilList = mobilList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BerandaMobilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_rental_mobil, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BerandaMobilAdapter.ViewHolder holder, int position) {
        MobilModel mobil = mobilList.get(position);

        holder.tvNama.setText(mobil.getNama_mobil());
        holder.tvTransmisi.setText("Transmisi: " + mobil.getTransmisi());

        // Format harga
        holder.tvHarga.setText(RupiahFormatter.format(mobil.getHarga_sewa()) + " / hari");

        // Status
        String status = mobil.getStatus() != null ? mobil.getStatus().trim() : "-";
        holder.tvStatus.setText(status);

        int color = status.equalsIgnoreCase("Tersedia") ?
                ContextCompat.getColor(context, R.color.green) :
                ContextCompat.getColor(context, R.color.red_primary);
        holder.tvStatus.setTextColor(color);

        // Total disewa
        holder.tvTotalDisewa.setText("Disewa: " + mobil.getTotal_disewa() + "x");

        // Gambar
        try {
            String image1 = mobil.getImage1() != null ? mobil.getImage1() : "";
            String encodedImage = URLEncoder.encode(image1, "UTF-8").replace("+", "%20");
            String url = "http://10.0.2.2/API_Rental_Mulia/uploads/" + encodedImage;

            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.bg_placeholder)
                    .error(R.drawable.bg_placeholder)
                    .into(holder.imageMobil);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Tombol sewa
        boolean isAvailable = "tersedia".equalsIgnoreCase(status);
        // Biarkan tombol selalu aktif
        holder.btnSewa.setEnabled(true);
        holder.btnSewa.setAlpha(1f);


        holder.btnDetail.setOnClickListener(v -> {
            if (listener != null) listener.onDetailClick(mobil);
        });

        holder.btnSewa.setOnClickListener(v -> {
            if (listener != null) listener.onSewaClick(mobil);
        });

    }


    @Override
    public int getItemCount() {
        return mobilList.size();
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
