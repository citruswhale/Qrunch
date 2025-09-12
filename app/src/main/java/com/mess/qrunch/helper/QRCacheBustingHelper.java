package com.mess.qrunch.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QRCacheBustingHelper {
    private static final String PREF_NAME = "QRCachePrefs";
    private static final String KEY_QR_IMAGE = "qrImageBase64";
    private static final String KEY_LAST_UPDATED = "lastUpdatedMonth";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save QR base64 string with current month
    public static void saveQR(Context context, String qrImageBase64) {
        String currentMonth = getCurrentMonth(); // e.g., "2025-09"

        getPrefs(context).edit()
                .putString(KEY_QR_IMAGE, qrImageBase64)
                .putString(KEY_LAST_UPDATED, currentMonth)
                .apply();
    }

    // Try to get cached image
    public static String getCachedQR(Context context) {
        SharedPreferences prefs = getPrefs(context);

        String savedMonth = prefs.getString(KEY_LAST_UPDATED, null);
        String currentMonth = getCurrentMonth();

        if (savedMonth == null || !savedMonth.equals(currentMonth)) {
            // Different month → clear cache
            clearCache(context);
            return null;
        }

        return prefs.getString(KEY_QR_IMAGE, null);
    }

    // Clear cache manually
    public static void clearCache(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    // Helper: "yyyy-MM" format (month-based)
    private static String getCurrentMonth() {
        return new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
    }
}