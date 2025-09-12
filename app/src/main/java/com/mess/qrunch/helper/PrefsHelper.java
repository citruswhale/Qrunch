package com.mess.qrunch.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsHelper {
    private static final String PREF_NAME = "MyAppPrefs";

    // Keys
    private static final String KEY_ROLL_NO = "rollNo";
    private static final String KEY_VENDOR_ID = "vendorId";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    // Save roll number
    public static void saveRollNo(Context context, String rollNo) {
        getPrefs(context).edit().putString(KEY_ROLL_NO, rollNo).apply();
    }

    public static String getRollNo(Context context) {
        return getPrefs(context).getString(KEY_ROLL_NO, null);
    }

    // Save vendorId (as Long)
    public static void saveVendorId(Context context, long vendorId) {
        getPrefs(context).edit().putLong(KEY_VENDOR_ID, vendorId).apply();
    }

    public static Long getVendorId(Context context) {
        long id = getPrefs(context).getLong(KEY_VENDOR_ID, -1L);
        return id == -1L ? null : id; // return null if not found
    }

    // Save name
    public static void saveName(Context context, String name) {
        getPrefs(context).edit().putString(KEY_NAME, name).apply();
    }

    public static String getName(Context context) {
        return getPrefs(context).getString(KEY_NAME, null);
    }

    // Save email
    public static void saveEmail(Context context, String email) {
        getPrefs(context).edit().putString(KEY_EMAIL, email).apply();
    }

    public static String getEmail(Context context) {
        return getPrefs(context).getString(KEY_EMAIL, null);
    }

    // Clear all (e.g., on logout)
    public static void clearAll(Context context) {
        getPrefs(context).edit().clear().apply();
    }

    // 🔹 Internal helper
    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
