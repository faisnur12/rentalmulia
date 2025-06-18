package com.example.rentalmobilmulia.ui.beranda;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rentalmobilmulia.R;
import com.example.rentalmobilmulia.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        // Auto greeting saat dibuka
        addBotReply("Halo! 👋 Ada yang bisa kami bantu hari ini?");

        // Tambahkan tombol quick replies
        addQuickReply("Daftar harga");
        addQuickReply("Tersedia hari ini?");
        addQuickReply("Syarat & Ketentuan");
        addQuickReply("Hubungi Admin");

        // Tombol kirim
        binding.btnSend.setOnClickListener(v -> {
            String message = binding.etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                addUserMessage(message);
                binding.etMessage.setText("");

                // Simulasi delay balasan otomatis 1.5 detik
                handler.postDelayed(() -> addBotReply(generateAutoReply(message)), 1500);
            }
        });

        return binding.getRoot();
    }

    private void addUserMessage(String message) {
        TextView tv = new TextView(getContext());
        tv.setText("Anda: " + message);
        tv.setPadding(16, 8, 16, 8);
        tv.setBackgroundResource(R.drawable.bg_user_bubble); // opsional untuk style bubble
        tv.setTextColor(getResources().getColor(R.color.black));
        binding.chatContainer.addView(tv);
        scrollToBottom();
    }

    private void addBotReply(String reply) {
        TextView tv = new TextView(getContext());
        tv.setText("Bot: " + reply);
        tv.setPadding(16, 8, 16, 8);
        tv.setTextColor(getResources().getColor(android.R.color.black));
        tv.setBackgroundResource(R.drawable.bg_bot_bubble); // opsional untuk style bubble
        binding.chatContainer.addView(tv);
        scrollToBottom();
    }

    private String generateAutoReply(String userMessage) {
        String msg = userMessage.toLowerCase();

        if (msg.contains("halo") || msg.contains("hai")) {
            return "Halo juga! Ada yang bisa kami bantu?";
        } else if (msg.contains("mobil") || msg.contains("sewa")) {
            return "Kami menyediakan berbagai pilihan mobil. Anda tertarik mobil tipe apa?";
        } else if (msg.contains("harga")) {
            return "Harga sewa mulai dari Rp250.000/hari tergantung tipe mobil.";
        } else if (msg.contains("tersedia")) {
            return "Silakan sebutkan tanggal & tipe mobil, kami bantu cek ketersediaannya.";
        } else if (msg.contains("admin")) {
            return "Silakan hubungi admin kami di WhatsApp: 0812-3456-7890";
        } else if (msg.contains("syarat")) {
            return "Syarat sewa: KTP, SIM A, dan DP minimal 30%.";
        } else if (msg.contains("terima kasih") || msg.contains("thanks")) {
            return "Sama-sama! 😊 Jika ada pertanyaan lain, jangan ragu ya!";
        } else {
            return "Mohon maaf, pesan Anda belum bisa kami pahami sepenuhnya.";
        }
    }

    private void addQuickReply(String text) {
        Button button = new Button(getContext());
        button.setText(text);
        button.setTextSize(13f);
        button.setPadding(20, 8, 20, 8);
        button.setBackgroundResource(R.drawable.bg_quick_reply);
        button.setTextColor(getResources().getColor(R.color.black));
        button.setAllCaps(false);
        button.setOnClickListener(v -> {
            binding.etMessage.setText(text);
            binding.btnSend.performClick();
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 16, 16);
        button.setLayoutParams(params);

        binding.quickRepliesContainer.addView(button);
    }


    private void scrollToBottom() {
        binding.scrollChat.post(() ->
                binding.scrollChat.fullScroll(View.FOCUS_DOWN)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
