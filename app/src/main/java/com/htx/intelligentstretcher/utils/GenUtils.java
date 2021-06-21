package com.htx.intelligentstretcher.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class GenUtils {
    public static Bitmap loadBitmapFromUriString(String imageUri) {
        File imgFile = new File(imageUri);

        if (imgFile.exists()) {
            return BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return null;
    }
}
