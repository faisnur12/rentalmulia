package com.example.rentalmobilmulia.ui.profil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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



        // Email
        view.findViewById(R.id.email_layout).setOnClickListener(v -> openEmail());

//        // Telepon
//        view.findViewById(R.id.phone_layout).setOnClickListener(v -> openPhone());

        // WhatsApp
        view.findViewById(R.id.wa_layout).setOnClickListener(v -> openWhatsApp());

        // Instagram
        view.findViewById(R.id.ig_layout).setOnClickListener(v -> openInstagram());

        // WebView Map
        WebView webViewMap = view.findViewById(R.id.webViewMap);
        webViewMap.setWebViewClient(new WebViewClient());

        WebSettings settings = webViewMap.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        String mapHtml = "<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d1980.5418599405562!2d109.07718578967288!3d-6.880573601522665!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x2e6fbbac7e8f0a27%3A0xd4e3007ec7348fbf!2sRental%20mobil%20MULIA!5e0!3m2!1sid!2sid!4v1736268062185!5m2!1sid!2sid\" width=\"100%\" height=\"100%\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>";
        String html = "<html><body style='margin:0;padding:0;'>" + mapHtml + "</body></html>";

        webViewMap.loadData(html, "text/html", "UTF-8");

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
        String phoneNumber = "628838833355";
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
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }
}
