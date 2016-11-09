package com.kasra.quickhuetoggle.core.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.kasra.quickhuetoggle.core.App;

import static com.kasra.quickhuetoggle.core.services.PrefsService.PREF_KEYS.BRIDGE_IP;
import static com.kasra.quickhuetoggle.core.services.PrefsService.PREF_KEYS.USERNAME;

public class PrefsService {
    private SharedPreferences prefs;
    protected static enum PREF_KEYS {
        USERNAME("username"),
        BRIDGE_IP("bridge_ip");

        private String val;
        PREF_KEYS(String val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return this.val;
        }
    }
    public PrefsService(App app) {
        prefs = app.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    private boolean containsKey(PREF_KEYS key) {
        return prefs.contains(key.toString());
    }

    private String getString(PREF_KEYS key) {
        return prefs.getString(key.toString(), "");
    }

    private void commitString(PREF_KEYS key, String value) {
        prefs.edit().putString(key.toString(), value).apply();
    }

    public boolean isUserLoggedIn() {
        return containsKey(USERNAME) && containsKey(BRIDGE_IP);
    }

    public String getBridgeIp() {
        return getString(BRIDGE_IP);
    }

    public String getApiUsername() {
        return getString(USERNAME);
    }

    public void setLoginInfo(String host, String username) {
        commitString(BRIDGE_IP, host);
        commitString(USERNAME, username);
    }
}
