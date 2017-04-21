package com.sardox.timestamper.Managers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sardox.timestamper.types.JetDuration;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.PhysicalLocation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager {
    private static final String SHARED_PREFS_SHOW_MILLIS = "SHOW_MILLIS";
    private static final String SHARED_PREFS_AUTONOTE = "AUTO_NOTE";
    private static final String SHARED_PREFS_TIMESTAMPS = "TIMESTAMPS";
    private static final String SHARED_PREFS_USE24HR = "USE_24HR";
    private static final String SHARED_PREFS_USEDARK = "USE_DARK";
    private static final String SHARED_PREFS_CATEGORIES = "CATEGORIES";
    private static final String SHARED_PREFS_USE_GPS = "USE_GPS";

    private SharedPreferences mPrefs;

    public DataManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public AppSettings readSettings() {
        AppSettings appSettings = new AppSettings();

        if (mPrefs.contains(SHARED_PREFS_AUTONOTE))
            appSettings.setShowNoteAddDialog(mPrefs.getBoolean(SHARED_PREFS_AUTONOTE, false));
        if (mPrefs.contains(SHARED_PREFS_SHOW_MILLIS)) {
            appSettings.setShowMillis(mPrefs.getBoolean(SHARED_PREFS_SHOW_MILLIS, false));
        }
        if (mPrefs.contains(SHARED_PREFS_USE24HR)) {
            appSettings.setUse24hrFormat(mPrefs.getBoolean(SHARED_PREFS_USE24HR, false));
        }
        if (mPrefs.contains(SHARED_PREFS_USE_GPS)) {
            appSettings.setUse_gps(mPrefs.getBoolean(SHARED_PREFS_USE_GPS, false));
        }
        if (mPrefs.contains(SHARED_PREFS_USEDARK)) {
            appSettings.setUseDark(mPrefs.getBoolean(SHARED_PREFS_USEDARK, true));
        }
        return appSettings;
    }

    public void writeSettings(AppSettings appSettings) {
        mPrefs.edit().putBoolean(SHARED_PREFS_SHOW_MILLIS, appSettings.isShowMillis()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USE24HR, appSettings.isUse24hrFormat()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_AUTONOTE, appSettings.isShowNoteAddDialog()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USEDARK, appSettings.isUseDark()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USE_GPS, appSettings.isUse_gps()).commit();
    }


    public List<Category> readCategories() {
        if (!hasPreviousData()) return sampleCategories();
        Gson gson = new Gson();
        String json = mPrefs.getString(SHARED_PREFS_CATEGORIES, "");
        Type type = new TypeToken<List<Category>>() {
        }.getType();

        return gson.fromJson(json, type);
    }

    public void writeCategories(List<Category> categories) {
        Gson gson = new Gson();
        String json = gson.toJson(categories);
        mPrefs.edit().putString(SHARED_PREFS_CATEGORIES, json).commit();
    }


    public HashMap<JetUUID, Timestamp> readTimestamps() {
        if (!hasPreviousData()) return sampleTimestamps();

        Gson gson = new Gson();
        String json = mPrefs.getString(SHARED_PREFS_TIMESTAMPS, "");
        Type type = new TypeToken<List<Timestamp>>() {
        }.getType();

        List<Timestamp> list = gson.fromJson(json,type);

        HashMap<JetUUID, Timestamp> hashMap = new HashMap<>();
        for (Timestamp timestamp : list)
           hashMap.put(timestamp.getIdentifier(), timestamp);

        return hashMap;
    }

    public void writeTimestamps(HashMap<JetUUID, Timestamp> timestamps) {
        Gson gson = new Gson();
        String json = gson.toJson(timestamps.values());
        mPrefs.edit().putString(SHARED_PREFS_TIMESTAMPS, json).commit();
    }


    private boolean hasPreviousData() {
        return (mPrefs.contains(SHARED_PREFS_USE_GPS));
    }

    private static List<Category> sampleCategories() {
        List<Category> sample = new ArrayList<>();
        sample.add(Category.Default);
        sample.add(new Category("Baby", categories().get(1), 1));
        sample.add(new Category("Sport", categories().get(2), 2));
        return sample;
    }

    private static HashMap<JetUUID, Timestamp> sampleTimestamps() {
        HashMap<JetUUID, Timestamp> sample = new HashMap<>();
        JetTimestamp now = JetTimestamp.now();

       Timestamp timestamp1 = new Timestamp(JetTimestamp.now(), PhysicalLocation.Default, categories().get(0), "Example: App was installed", JetUUID.randomUUID());
       Timestamp timestamp2 = new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() - 1000 * 60 * 4), PhysicalLocation.Default, categories().get(1), "Example: Changed diapers", JetUUID.randomUUID());
       Timestamp timestamp3 = new Timestamp(JetTimestamp.now().subtract(JetDuration.fromDays(5)), PhysicalLocation.Default, categories().get(2), "Example: came to gym", JetUUID.randomUUID());
        sample.put(timestamp1.getIdentifier(),timestamp1);
        sample.put(timestamp2.getIdentifier(),timestamp2);
        sample.put(timestamp3.getIdentifier(),timestamp3);

        return sample;
    }

    private static List<JetUUID> categories() {
        List<JetUUID> jetList = new ArrayList<>();
        jetList.add(JetUUID.Zero);
        jetList.add(JetUUID.fromString("1cefd5bc-ebc6-493b-9f4e-e23591d1d001"));
        jetList.add(JetUUID.fromString("1cefd5bc-ebc6-493b-9f4e-e23591d1d002"));
        return jetList;
    }
}
