package com.mess.qrunch.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MenuCacheBustingHelper {
    private static final String PREF_NAME = "ImageCachePrefs";
    private static final String KEY_IMAGE_URL = "imageUrl";
    private static final String KEY_LAST_UPDATED = "lastUpdatedDate";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Save image URL with today's date (cache-busting query param)
    public static void saveImage(Context context, String imageUrl) {
        String today = getTodayDate(); // e.g., "2025-09-03"

        getPrefs(context).edit()
                .putString(KEY_IMAGE_URL, imageUrl + "?ver=" + today) // cache-busting
                .putString(KEY_LAST_UPDATED, today)
                .apply();
    }

    // Try to get cached image
    public static String getCachedImage(Context context) {
        SharedPreferences prefs = getPrefs(context);

        String savedDate = prefs.getString(KEY_LAST_UPDATED, null);
        String today = getTodayDate();

        if (savedDate == null || !savedDate.equals(today)) {
            // Not today's → clear cache
            clearCache(context);
            return null;
        }

        return prefs.getString(KEY_IMAGE_URL, null);
    }

    // Clear cache manually
    public static void clearCache(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    // Helper: today's date as yyyy-MM-dd
    private static String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
}