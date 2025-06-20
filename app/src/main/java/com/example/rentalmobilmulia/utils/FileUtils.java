package com.example.rentalmobilmulia.utils;

import android.content.Context;
import android.net.Uri;

import java.io.*;

public class FileUtils {
    public static File getFile(Context context, Uri uri) {
        File file = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            String fileName = "upload_" + System.currentTimeMillis() + ".jpg";
            file = new File(context.getCacheDir(), fileName);
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                outputStream.write(buffer, 0, bytesRead);

            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}
