package com.example.rentalmobilmulia.ui.profil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rentalmobilmulia.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactsFragment extends Fragment {

    private MapView mapView;

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

        // WhatsApp
        LinearLayout waLayout = view.findViewById(R.id.wa_layout);
        waLayout.setOnClickListener(v -> openWhatsApp());

        // Instagram
        LinearLayout igLayout = view.findViewById(R.id.ig_layout);
        igLayout.setOnClickListener(v -> openInstagram());

        // Lokasi
        mapView = view.findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.onResume();

            try {
                MapsInitializer.initialize(requireActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mapView.getMapAsync(googleMap -> {
                LatLng lokasi = new LatLng(-6.86997, 109.12559); // Lokasi Tegal
                googleMap.addMarker(new MarkerOptions().position(lokasi).title("Mulia Rent Car"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15));
            });
        }

        return view;
    }

    private void openEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:maxlitestore@gmail.com"));
        startActivity(intent);
    }

    private void openWhatsApp() {
        String phoneNumber = "6287839383057"; // nomor WA tanpa tanda +
        String message = "Halo, saya tertarik dengan layanan rental mobil Anda";

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
        Uri uri = Uri.parse("https://www.instagram.com/muliarentcartegal");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.instagram.android");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, uri)); // Fallback ke browser
        }
    }

    // Lifecycle MapView agar tidak crash
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mapView != null) mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }
}
