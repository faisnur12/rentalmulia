package com.example.rentalmobilmulia.ui.pesanan;

import java.text.NumberFormat;
import java.util.Locale;

public class RupiahFormatter {
    public static String format(double value) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(value);
    }
}
