package com.sardox.timestamper.Managers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.QuickNoteList;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.utils.AppSettings;
import com.sardox.timestamper.utils.Constants;
import com.sardox.timestamper.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DataManager {
    private final String newLine = "\n";
    private final String newComma = ",";
    private SharedPreferences mPrefs;

    public DataManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public AppSettings loadUserSettings() {
        AppSettings appSettings = new AppSettings();

        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_AUTONOTE))
            appSettings.setShowNoteAddDialog(mPrefs.getBoolean(Constants.Settings.SHARED_PREFS_AUTONOTE, false));
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_SHOW_MILLIS)) {
            appSettings.setShouldShowMillis(mPrefs.getBoolean(Constants.Settings.SHARED_PREFS_SHOW_MILLIS, false));
        }
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_USE24HR)) {
            appSettings.setUse24hrFormat(mPrefs.getBoolean(Constants.Settings.SHARED_PREFS_USE24HR, false));
        }
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_USE_GPS)) {
            appSettings.setShouldUseGps(mPrefs.getBoolean(Constants.Settings.SHARED_PREFS_USE_GPS, false));
        }
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_USE_QUICK_NOTES)) {
            appSettings.setShouldUseQuickNotes(mPrefs.getBoolean(Constants.Settings.SHARED_PREFS_USE_QUICK_NOTES, true));
        }
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_SHOW_KEYBOARD)) {
            appSettings.setShouldShowKeyboardInAddNote(mPrefs.getBoolean(Constants.Settings.SHARED_PREFS_SHOW_KEYBOARD, false));
        }
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_USE_DARK_THEME)) {
            appSettings.setShouldUseDarkTheme(mPrefs.getBoolean(Constants.Settings.SHARED_PREFS_USE_DARK_THEME, true));
        }
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_QUICK_NOTES)) {
            appSettings.setQuickNotes(readQuickNotes());
        }
        return appSettings;
    }

    public void writeUserNotes(AppSettings appSettings) {
        writeQuickNotes(appSettings.getQuickNotes());
    }

    private void writeQuickNotes(QuickNoteList quickNoteList) {
        Gson gson = new Gson();
        String json = gson.toJson(quickNoteList);
        mPrefs.edit().putString(Constants.Settings.SHARED_PREFS_QUICK_NOTES, json).apply();
    }

    private QuickNoteList readQuickNotes() {
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.Settings.SHARED_PREFS_QUICK_NOTES, "");
        Type type = new TypeToken<QuickNoteList>() {
        }.getType();
        QuickNoteList quickNoteList = gson.fromJson(json, type);
        if (quickNoteList != null && !quickNoteList.isEmpty()) {
            return quickNoteList;
        } else {
            return new QuickNoteList();
        }
    }

    public List<Category> readCategories() {
        if (!hasPreviousData()) return Utils.getSampleCategories();
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.Settings.SHARED_PREFS_CATEGORIES, "");
        Type type = new TypeToken<List<Category>>() {
        }.getType();
        List<Category> categories = gson.fromJson(json, type);
        if (categories != null && !categories.isEmpty()) {
            return categories;
        } else {
            return new ArrayList<>();
        }
    }

    public void writeCategories(List<Category> categories) {
        Gson gson = new Gson();
        String json = gson.toJson(categories);
        mPrefs.edit().putString(Constants.Settings.SHARED_PREFS_CATEGORIES, json).commit();
    }

    public HashMap<JetUUID, Timestamp> readTimestamps() {
        if (!hasPreviousData()) return Utils.getSampleTimestamps();
        return getTimestampsByKey(Constants.Settings.SHARED_PREFS_TIMESTAMPS);
    }

    public HashMap<JetUUID, Timestamp> readWidgetTimestamps() {
        if (!hasPreviousData()) return new HashMap<>();
        return getTimestampsByKey(Constants.Settings.SHARED_PREFS_WIDGET_TIMESTAMPS);
    }

    private HashMap<JetUUID, Timestamp> getTimestampsByKey(String sharedPrefsKey) {
        Gson gson = new Gson();
        String json = mPrefs.getString(sharedPrefsKey, "");
        Type type = new TypeToken<List<Timestamp>>() {
        }.getType();

        List<Timestamp> list = gson.fromJson(json, type);
        HashMap<JetUUID, Timestamp> hashMap = new HashMap<>();
        if (list != null && !list.isEmpty()) {
            for (Timestamp timestamp : list) {
                hashMap.put(timestamp.getIdentifier(), timestamp);
            }
        }
        Log.v("sardox2", "-----------readTimestamps from " + sharedPrefsKey + " . Total: " + hashMap.size());
        return hashMap;
    }

    public void writeTimestamps(HashMap<JetUUID, Timestamp> timestamps) {
        writeTimestamps(timestamps, Constants.Settings.SHARED_PREFS_TIMESTAMPS);
    }

    public void writeWidgetTimestamps(HashMap<JetUUID, Timestamp> timestamps) {
        writeTimestamps(timestamps, Constants.Settings.SHARED_PREFS_WIDGET_TIMESTAMPS);
    }

    public void clearWidgetTimestamps() {
        writeTimestamps(new HashMap<JetUUID, Timestamp>(), Constants.Settings.SHARED_PREFS_WIDGET_TIMESTAMPS);
    }

    private void writeTimestamps(HashMap<JetUUID, Timestamp> timestamps, String sharedPrefsKey) {
        Gson gson = new Gson();
        String json = gson.toJson(timestamps.values());
        mPrefs.edit().putString(sharedPrefsKey, json).commit();
    }

    private boolean hasPreviousData() {
        return (mPrefs.contains(Constants.Settings.SHARED_PREFS_USE_GPS));
    }

    private List<Timestamp> sortDesc(List<Timestamp> timestamps) {
        Collections.sort(timestamps, new Comparator<Timestamp>() {
            public int compare(Timestamp t1, Timestamp t2) {
                return t2.getTimestamp().compareTo(t1.getTimestamp());
            }
        });
        return timestamps;
    }

    public File exportToCSV(Category category, List<Timestamp> timestamps, List<Category> categories) {
        String plainText;
        if (category.getCategoryID().equals(JetUUID.Zero)) {
            plainText = prepareAllTimestampsForExport(timestamps, categories);
        } else {
            plainText = prepareCategoryTimestampsForExport(timestamps, category);
        }
        Log.d("RawFile", plainText);

        File root = Environment.getExternalStorageDirectory();
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Log.v("srdx", "sdcard mounted and writable");
            File dir = new File(root.getAbsolutePath());
            dir.getParentFile().mkdirs();
            try {
                File file = new File(dir, Constants.EXPORT_FILE_NAME);
                FileOutputStream out = new FileOutputStream(file);
                out.write(plainText.getBytes());
                out.close();
                return file;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("srdx", "FileNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("srdx", "IOException");
            }
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            Log.e("srdx", "sdcard mounted readonly");
        } else {
            Log.e("srdx", "sdcard state: " + state);
        }
        return null;
    }

    private String prepareCategoryTimestampsForExport(List<Timestamp> timestamps, Category category) {
        final Calendar calendar = Calendar.getInstance();
        final TimeZone localTZ = calendar.getTimeZone();
        StringBuilder plainText = new StringBuilder(category.getName() + newLine);
        for (Timestamp item : sortDesc(timestamps)) {
            if (item.getCategoryId().equals(category.getCategoryID())) {
                plainText.append(item.getTimestamp().toString(Locale.getDefault(), localTZ)).append(newComma);
                plainText.append(item.getNote()).append(newComma);
                plainText.append(item.getPhysicalLocation().toCsvString());
                plainText.append(newLine);
            }
        }
        calendar.clear();
        return plainText.toString();
    }

    private String getCategoryNameByUUID(List<Category> categories, JetUUID uuid) {
        for (Category category : categories) {
            if (category.getCategoryID().equals(uuid)) {
                return category.getName();
            }
        }
        return "Unknown";
    }

    private String prepareAllTimestampsForExport(List<Timestamp> timestamps, List<Category> categories) {
        final Calendar calendar = Calendar.getInstance();
        final TimeZone localTZ = calendar.getTimeZone();
        StringBuilder plainText = new StringBuilder("All categories export" + newLine);
        for (Timestamp item : sortDesc(timestamps)) {
            plainText.append(item.getTimestamp().toString(Locale.getDefault(), localTZ)).append(newComma);
            plainText.append(getCategoryNameByUUID(categories, item.getCategoryId())).append(newComma);
            plainText.append(item.getNote()).append(newComma);
            plainText.append(item.getPhysicalLocation().toCsvString());
            plainText.append(newLine);
        }
        calendar.clear();
        return plainText.toString();
    }

    public void saveDefaultCategoryForWidget(JetUUID selectedCategoryId) {
        mPrefs.edit().putString(Constants.Settings.SHARED_PREFS_WIDGET_DEFAULT_TIMESTAMP, selectedCategoryId.toUuid().toString()).apply();
    }

    public JetUUID readDefaultCategoryForWidget() {
        if (mPrefs.contains(Constants.Settings.SHARED_PREFS_WIDGET_DEFAULT_TIMESTAMP)) {
            return JetUUID.fromString(mPrefs.getString(Constants.Settings.SHARED_PREFS_WIDGET_DEFAULT_TIMESTAMP, AppSettings.Companion.getNO_DEFAULT_CATEGORY().toString()));
        } else {
            return AppSettings.Companion.getNO_DEFAULT_CATEGORY();
        }
    }

}
