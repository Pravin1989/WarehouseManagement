package com.amiablecore.warehouse.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;

    public Session(Context cntx) {
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void putToSession(String key, Object value) {
        prefs.edit().putString(key, value.toString()).commit();
    }

    public String getFromSession(String key) {
        String usename = prefs.getString(key, "");
        return usename;
    }

    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }
}
