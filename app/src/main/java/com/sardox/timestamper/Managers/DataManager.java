package com.sardox.timestamper.Managers;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sardox.timestamper.types.JetDuration;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.PhysicalLocation;
import com.sardox.timestamper.utils.TimestampIcon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataManager {
    private static final String SHARED_PREFS_USE_GPS = "gps";
    private static final String SHARED_PREFS_STAMPS = "stamps";
    private static final String SHARED_PREFS_CATS = "cats";
    private static final String SHARED_PREFS_USE24HR = "use24hr";
    private static final String SHARED_PREFS_AUTONOTE = "autoNote";
    private static final String SHARED_PREFS_USEDARK = "useDark";
    private static final String SHARED_PREFS_SHOW_MILLIS = "showMillis";

    private SharedPreferences mPrefs;

    public DataManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public List<Timestamp> readTimestamps() {
        return sampleTimestamps();
    }

    public HashMap<JetUUID, Timestamp> readTimestampsMap() {
        HashMap<JetUUID, Timestamp> hashMap = new HashMap<>();
        for (Timestamp timestamp : sampleTimestamps()) hashMap.put(timestamp.getIdentifier(),timestamp);
        return hashMap;
    }


    public void writeTimestamps() {
    }

    public List<Category> readCategories() {
        return sampleCategories();
    }

    public void writeCategories() {
    }

    public AppSettings readSettings() {
        AppSettings appSettings = new AppSettings();

        if (mPrefs.contains(SHARED_PREFS_USE_GPS))
            appSettings.setUse_gps(mPrefs.getBoolean(SHARED_PREFS_USE_GPS, false));
        if (mPrefs.contains(SHARED_PREFS_USEDARK))
            appSettings.setUseDark(mPrefs.getBoolean(SHARED_PREFS_USEDARK, true));
        if (mPrefs.contains(SHARED_PREFS_SHOW_MILLIS))
            appSettings.setShowMillis(mPrefs.getBoolean(SHARED_PREFS_SHOW_MILLIS, false));
        if (mPrefs.contains(SHARED_PREFS_USE24HR))
            appSettings.setUse24hrFormat(mPrefs.getBoolean(SHARED_PREFS_USE24HR, false));
        if (mPrefs.contains(SHARED_PREFS_AUTONOTE))
            appSettings.setShowNoteAddDialog(mPrefs.getBoolean(SHARED_PREFS_AUTONOTE, false));
        return appSettings;
    }

    public void writeSettings(AppSettings appSettings) {

        mPrefs.edit().putBoolean(SHARED_PREFS_SHOW_MILLIS, appSettings.isShowMillis()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USE24HR, appSettings.isUse24hrFormat()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_AUTONOTE, appSettings.isShowNoteAddDialog()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USEDARK, appSettings.isUseDark()).commit();
        mPrefs.edit().putBoolean(SHARED_PREFS_USE_GPS, appSettings.isUse_gps()).commit();
    }

    public boolean noPreviousData() {
        return true;
    }

    private List<Timestamp> sampleTimestamps() {
        List<Timestamp> sample = new ArrayList<>();
        JetTimestamp now = JetTimestamp.now();

        sample.add(new Timestamp(JetTimestamp.now(), PhysicalLocation.Default, categories().get(0), "real time timestamp", JetUUID.randomUUID()));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() - 1000 * 60 * 4), PhysicalLocation.Default, categories().get(0), "add a note", JetUUID.randomUUID()));
        sample.add(new Timestamp(JetTimestamp.now().subtract(JetDuration.fromDays(5)),                PhysicalLocation.Default, categories().get(1), "add a note", JetUUID.randomUUID()));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() - 1000 * 60 * 7), PhysicalLocation.Default, categories().get(1), "add a note", JetUUID.randomUUID()));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() - 1000 * 60 * 8), PhysicalLocation.Default, categories().get(2), "add a note", JetUUID.randomUUID()));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() - 1000 * 60 * 10), PhysicalLocation.Default,categories().get(2), "add a note", JetUUID.randomUUID()));
        return sample;
    }

    private List<Category> sampleCategories() {
        List<Category> sample = new ArrayList<>();
        sample.add(Category.Default);
        sample.add(new Category("Baby", categories().get(1), 1));
        sample.add(new Category("Sport", categories().get(2), 2));
        sample.add(new Category("Home", categories().get(3), 3));
        return sample;
    }

    private  final static  List<JetUUID> categories(){
        List<JetUUID> jetList = new ArrayList<>();
        jetList.add(JetUUID.Zero);
        jetList.add(JetUUID.fromString("1cefd5bc-ebc6-493b-9f4e-e23591d1d001"));
        jetList.add(JetUUID.fromString("1cefd5bc-ebc6-493b-9f4e-e23591d1d002"));
        jetList.add(JetUUID.fromString("1cefd5bc-ebc6-493b-9f4e-e23591d1d003"));
        return  jetList;
    }
}
