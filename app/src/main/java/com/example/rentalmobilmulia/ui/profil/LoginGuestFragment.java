package com.example.rentalmobilmulia.ui.profil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rentalmobilmulia.Login;
import com.example.rentalmobilmulia.R;


public class LoginGuestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login_guest, container, false);

        if (getActivity() != null) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("login_pref", getActivity().MODE_PRIVATE).edit();
            editor.putBoolean("isLoggedIn", false);
            editor.putString("email", "");
            editor.putString("nama", "Tamu");
            editor.apply();
        }

        Button btnLoginGuest = view.findViewById(R.id.btnLoginNow);
        btnLoginGuest.setOnClickListener(v -> {
            if (getActivity() != null) {
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
