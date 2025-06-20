package com.example.rentalmobilmulia.ui.profil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.RetrofitClient;
import com.example.rentalmobilmulia.model.SewaModel;

import java.util.List;

public class RiwayatSewaAdapter extends RecyclerView.Adapter<RiwayatSewaAdapter.ViewHolder> {

    private final Context context;
    private final List<SewaModel> list;
    private final OnUploadClickListener uploadClickListener;

    public interface OnUploadClickListener {
        void onUploadClicked(SewaModel sewa);
    }

    public RiwayatSewaAdapter(Context context, List<SewaModel> list, OnUploadClickListener listener) {
        this.context = context;
        this.list = list;
        this.uploadClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_riwayat_sewa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SewaModel item = list.get(position);

        // Gambar Mobil
        Glide.with(context)
                .load(RetrofitClient.BASE_URL_IMAGE + item.getFotoMobil())
                .placeholder(R.drawable.sample_mobil)
                .into(holder.imgMobil);

        // Kode Booking + Nama
        holder.tvKode.setText("No. " + (position + 1) + " | Kode: " + item.getKodeBooking());
        holder.tvNamaMobil.setText("Nama Mobil: " + item.getNamaMobil());

        // Tanggal Sewa
        holder.tvTanggalSewa.setText("Tgl. Mulai: " + item.getTglMulai() + " | Tgl. Selesai: " + item.getTglSelesai());

        // Durasi
        holder.tvDurasi.setText("Durasi: " + item.getDurasi() + " Hari");

        // Total Biaya (biaya mobil dan driver disatukan di field total)
        holder.tvTotalBiaya.setText("Total: Rp" + item.getTotal());

        // Status
        holder.tvStatus.setText("Status: " + item.getStatus());

        // Tampilkan atau sembunyikan rekening dan upload bukti sesuai status
        if (item.getStatus() != null && item.getStatus().toLowerCase().contains("bayar")) {
            holder.tvRekening.setVisibility(View.VISIBLE);
            holder.btnUpload.setVisibility(View.VISIBLE);
        } else {
            holder.tvRekening.setVisibility(View.GONE);
            holder.btnUpload.setVisibility(View.GONE);
        }


        // Gambar bukti pembayaran
        if (item.getBuktiBayar() != null && !item.getBuktiBayar().isEmpty()) {
            holder.imgBuktiBayar.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(RetrofitClient.BASE_URL_IMAGE + item.getBuktiBayar())
                    .placeholder(R.drawable.sample_bukti)
                    .into(holder.imgBuktiBayar);
        } else {
            holder.imgBuktiBayar.setVisibility(View.VISIBLE); // tetap tampil
            holder.imgBuktiBayar.setImageResource(R.drawable.sample_bukti); // default image
        }


        // Upload klik
        holder.btnUpload.setOnClickListener(v -> {
            if (uploadClickListener != null) {
                uploadClickListener.onUploadClicked(item); // kirim item ke Fragment
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMobil, imgBuktiBayar;
        TextView tvKode, tvNamaMobil, tvTanggalSewa, tvDurasi, tvTotalBiaya, tvStatus, tvRekening;
        Button btnUpload;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMobil = itemView.findViewById(R.id.imgMobil);
            imgBuktiBayar = itemView.findViewById(R.id.imgBuktiBayar);
            tvKode = itemView.findViewById(R.id.tvKodeSewa);
            tvNamaMobil = itemView.findViewById(R.id.tvNamaMobil);
            tvTanggalSewa = itemView.findViewById(R.id.tvTanggalSewa);
            tvDurasi = itemView.findViewById(R.id.tvDurasi);
            tvTotalBiaya = itemView.findViewById(R.id.tvTotalBiaya);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRekening = itemView.findViewById(R.id.tvRekening);
            btnUpload = itemView.findViewById(R.id.btnUploadBukti);
        }
    }
}
