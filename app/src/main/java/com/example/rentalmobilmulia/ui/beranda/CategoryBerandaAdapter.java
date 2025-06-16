package com.example.rentalmobilmulia.ui.beranda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.rentalmobilmulia.R;

import java.util.List;

/**
 * Adapter untuk menampilkan daftar kategori pada HomeFragment.
 */
public class CategoryBerandaAdapter extends RecyclerView.Adapter<CategoryBerandaAdapter.CategoryViewHolder> {

    // Interface listener untuk menangani klik pada kategori
    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryBeranda category);
    }

    private final List<CategoryBeranda> categoryList;
    private final OnCategoryClickListener listener;

    // Constructor
    public CategoryBerandaAdapter(List<CategoryBeranda> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_home, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryBeranda category = categoryList.get(position);

        // Set nama dan icon kategori
        holder.tvNama.setText(category.getNama());
        holder.ivIcon.setImageResource(category.getIconResId());

        // Tangani klik item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    /**
     * ViewHolder untuk item kategori
     */
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvNama;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIconCategory);
            tvNama = itemView.findViewById(R.id.tvNamaCategory);
        }
    }
}
