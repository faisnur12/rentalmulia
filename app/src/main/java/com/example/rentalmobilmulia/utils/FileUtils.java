package com.example.rentalmobilmulia.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;

public class FileUtils {
    public static File getFile(Context context, Uri uri) {
        String path = RealPathUtil.getRealPath(context, uri);
        return new File(path);
    }
}

