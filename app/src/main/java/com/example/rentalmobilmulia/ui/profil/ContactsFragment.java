package com.example.rentalmobilmulia.ui.profil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.rentalmobilmulia.R;


public class ContactsFragment extends Fragment {

    public ContactsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Tombol back
        LinearLayout btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Email
        LinearLayout emailLayout = view.findViewById(R.id.email_layout);
        emailLayout.setOnClickListener(v -> openEmail());

        // Telepon
        LinearLayout phoneLayout = view.findViewById(R.id.phone_layout);
        phoneLayout.setOnClickListener(v -> openPhone());

        // WhatsApp
        LinearLayout waLayout = view.findViewById(R.id.wa_layout);
        waLayout.setOnClickListener(v -> openWhatsApp());

        // Instagram
        LinearLayout igLayout = view.findViewById(R.id.ig_layout);
        igLayout.setOnClickListener(v -> openInstagram());

        // Lokasi
        LinearLayout locationLayout = view.findViewById(R.id.location_layout);
        locationLayout.setOnClickListener(v -> openMaps());

        return view;
    }

    private void openEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:maxlitestore@gmail.com"));
        startActivity(intent);
    }

    private void openPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:+6281938833355"));
        startActivity(intent);
    }

    private void openWhatsApp() {
        String phoneNumber = "6281938833355"; // Tanpa tanda +
        String message = "Halo, saya tertarik dengan produk Anda";

        try {
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(message));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Terjadi kesalahan saat membuka WhatsApp", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openInstagram() {
        Uri uri = Uri.parse("https://www.instagram.com/gajahmotorsemarang?igsh=ZHo1ZzRuMm94OGZz");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.instagram.android");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, uri)); // Fallback ke browser
        }
    }

    private void openMaps() {
        Uri uri = Uri.parse("https://maps.app.goo.gl/hwsu7H45bXYcGMDL8");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
            Toast.makeText(getActivity(), "Google Maps tidak ditemukan, membuka lewat browser", Toast.LENGTH_SHORT).show();
        }
    }
}
