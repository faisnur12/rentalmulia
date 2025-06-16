package com.example.rentalmobilmulia.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class RupiahFormatter {

    public static String format(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String result = formatter.format(amount);
        return result.replace("Rp", "Rp ").replace(",00", "");
    }

    public static String format(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        String result = formatter.format(amount);
        return result.replace("Rp", "Rp ").replace(",00", "");
    }

}
