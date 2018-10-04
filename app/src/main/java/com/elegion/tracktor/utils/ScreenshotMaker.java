package com.elegion.tracktor.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ScreenshotMaker {

    public static Bitmap getScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache(true);
        view.getDrawingCache(false);

        return bitmap;
    }

    public static String toBase64(Bitmap bitmap, int quality) {
        return Base64.encodeToString(getBytes(bitmap, quality), Base64.DEFAULT);
    }

    public static Bitmap fromBase64(String base64) {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length);
    }

    private static byte[] getBytes(Bitmap bitmap, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
