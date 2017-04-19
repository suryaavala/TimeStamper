package com.sardox.timestamper.Managers;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.PhysicalLocation;
import com.sardox.timestamper.utils.TimestampIcon;

import java.util.ArrayList;
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

        sample.add(new Timestamp(JetTimestamp.now(), PhysicalLocation.Default, 0, "Default 1"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000 * 60 * 4), PhysicalLocation.Default, 0, "Default 2"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000 * 60 * 7), PhysicalLocation.Default, 1, "Sport 3"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000 * 60 * 8), PhysicalLocation.Default, 1, "Sport 5"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000 * 60 * 6), PhysicalLocation.Default, 2, "Baby 5"));
        sample.add(new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() + 1000 * 60 * 10), PhysicalLocation.Default, 2, "Baby 6 "));
        return sample;
    }

    private List<Category> sampleCategories() {
        List<Category> sample = new ArrayList<>();
        sample.add(new Category("Default", 0, 0));
        sample.add(new Category("Baby", 1, 1));
        sample.add(new Category("Sport", 2, 2));
        sample.add(new Category("Home", 3, 3));
        return sample;
    }
}
