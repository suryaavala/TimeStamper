package com.sardox.timestamper.utils;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetDuration;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class Utils {

    public static List<String> getListOfCategories(List<Category> allCategories) {
        List<String> quickCategories = new ArrayList<>();
        for (int a = 0; a < allCategories.size(); a++) {
            quickCategories.add(allCategories.get(a).getName());
        }
        return quickCategories;
    }

    public static void removeTimestampsByCategory(HashMap<JetUUID, Timestamp> allTimestamps, Category category) {
        for (JetUUID id : getTimestampsFromCategory(allTimestamps, category)) {
            allTimestamps.remove(id);
        }
    }

    private static List<JetUUID> getTimestampsFromCategory(HashMap<JetUUID, Timestamp> allTimestamps, Category desiredCategory) {
        List<JetUUID> categorizedTimestamps = new ArrayList<>();
        for (Timestamp timestamp : allTimestamps.values()) {
            if (timestamp.getCategoryId().equals(desiredCategory.getCategoryID()))
                categorizedTimestamps.add(timestamp.getIdentifier());
        }
        return categorizedTimestamps;
    }

    public static List<Timestamp> filterTimestampsByCategory(HashMap<JetUUID, Timestamp> allTimestamps, Category selectedCategory) {
        List<Timestamp> filteredList = new ArrayList<>();
        if (selectedCategory.equals(Category.Default)) {
            filteredList.addAll(allTimestamps.values());
        } else {
            for (Timestamp timestamp : allTimestamps.values()) {
                if (timestamp.getCategoryId().equals(selectedCategory.getCategoryID()))
                    filteredList.add(timestamp);
            }
        }
        return filteredList;
    }

    public static void emailCSV(Context context, File file) {
        if (file != null) {
            Uri u1 = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.EXPORT_NAME);
            sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
            sendIntent.setType(Constants.EXPORT_FILE_TYPE);
            context.startActivity(sendIntent);
        }
    }

    public static List<TimestampIcon> getStockIcons() {
        List<TimestampIcon> icons = new ArrayList<>();
        icons.add(new TimestampIcon(R.drawable.category_default, "DEFAULT", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_baby, "BABY", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_sport, "SPORT", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_home, "HOME", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_love, "FAVORITE", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_pill, "PILLS", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_sleep, "SLEEP", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_car, "CAR", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_food, "FOOD", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_map, "MAP", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_phone, "PHONE", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_timer, "TIMER", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_wallet, "MONEY", icons.size()));
        icons.add(new TimestampIcon(R.drawable.category_weather, "WEATHER", icons.size()));
        icons.add(new TimestampIcon(R.drawable.zzz, "ZZZ", icons.size()));
        icons.add(new TimestampIcon(R.drawable.train, "TRAVEL", icons.size()));
        icons.add(new TimestampIcon(R.drawable.duck, "PLAY", icons.size()));
        icons.add(new TimestampIcon(R.drawable.call, "CALLS", icons.size()));
        icons.add(new TimestampIcon(R.drawable.workout, "WORKOUT", icons.size()));
        icons.add(new TimestampIcon(R.drawable.hospital, "HOSPITAL", icons.size()));
        icons.add(new TimestampIcon(R.drawable.education, "EDUCATION", icons.size()));
        icons.add(new TimestampIcon(R.drawable.fruits, "FRUITS", icons.size()));
        icons.add(new TimestampIcon(R.drawable.water, "WATER", icons.size()));
        icons.add(new TimestampIcon(R.drawable.paper, "PAPER", icons.size()));
        return icons;
    }

    public static List<Category> getSampleCategories() {
        List<JetUUID> sampleCategoriesIds = getSampleCategoriesIds();
        List<Category> sampleCategories = new ArrayList<>();
        sampleCategories.add(Category.Default);
        sampleCategories.add(new Category("SLEEP", sampleCategoriesIds.get(1), 14));
        sampleCategories.add(new Category("PILLS", sampleCategoriesIds.get(2), 5));
        return sampleCategories;
    }

    public static HashMap<JetUUID, Timestamp> getSampleTimestamps() {
        List<JetUUID> sampleCategoriesIds = getSampleCategoriesIds();
        HashMap<JetUUID, Timestamp> sample = new HashMap<>();
        JetTimestamp now = JetTimestamp.now();
        Timestamp timestamp1 = new Timestamp(JetTimestamp.now(), PhysicalLocation.Default, sampleCategoriesIds.get(0), "Example: App was installed", JetUUID.randomUUID());
        Timestamp timestamp2 = new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() - TimeUnit.HOURS.toMillis(9)), PhysicalLocation.Default, sampleCategoriesIds.get(1), "Example: fall asleep", JetUUID.randomUUID());
        Timestamp timestamp3 = new Timestamp(JetTimestamp.fromMilliseconds(now.toMilliseconds() - TimeUnit.HOURS.toMillis(2)), PhysicalLocation.Default, sampleCategoriesIds.get(1), "Example: woke up", JetUUID.randomUUID());
        Timestamp timestamp4 = new Timestamp(JetTimestamp.now().subtract(JetDuration.fromDays(3)), PhysicalLocation.Default, sampleCategoriesIds.get(2), "Example: minor headache. took 2 pills", JetUUID.randomUUID());
        Timestamp timestamp5 = new Timestamp(JetTimestamp.now().subtract(JetDuration.fromDays(4)), PhysicalLocation.Default, sampleCategoriesIds.get(2), "Example: took vitamins", JetUUID.randomUUID());
        sample.put(timestamp1.getIdentifier(), timestamp1);
        sample.put(timestamp2.getIdentifier(), timestamp2);
        sample.put(timestamp3.getIdentifier(), timestamp3);
        sample.put(timestamp4.getIdentifier(), timestamp4);
        sample.put(timestamp5.getIdentifier(), timestamp5);
        return sample;
    }

    private static List<JetUUID> getSampleCategoriesIds() {
        List<JetUUID> jetList = new ArrayList<>();
        jetList.add(JetUUID.Zero);
        jetList.add(JetUUID.fromString("1cefd5bc-ebc6-493b-9f4e-e23591d1d001"));
        jetList.add(JetUUID.fromString("1cefd5bc-ebc6-493b-9f4e-e23591d1d002"));
        return jetList;
    }

    public static String getPhraseOfTheDay(Context context) {
        final String[] mTestArray = context.getResources().getStringArray(R.array.phrases);
        Random r = new Random();
        int phraseToPick = r.nextInt(mTestArray.length);
        return mTestArray[phraseToPick];
    }

    public static HashMap<JetUUID, Timestamp> listToHashMap(List<Timestamp> list) {
        HashMap<JetUUID, Timestamp> hashMap = new HashMap<>();
        for (Timestamp t : list) hashMap.put(t.getIdentifier(), t);
        return hashMap;
    }

    public static List<Timestamp> getDeepCopyOfTimestampList(List<Timestamp> toCopy) {
        Collection<Timestamp> copy = new HashSet<>(toCopy.size());
        for (Timestamp selectedTimestamp : toCopy) {
            try {
                copy.add((Timestamp) selectedTimestamp.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(copy);
    }

    public static void updateGridWidget(Context applicationContext) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(applicationContext);
        ComponentName thisWidget = new ComponentName(applicationContext, GridWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gridview);
    }
}
