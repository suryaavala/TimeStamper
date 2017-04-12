package com.sardox.timestamper.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;


import com.sardox.timestamper.R;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class InstantTimestampWidget extends AppWidgetProvider {

    private static final String SYNC_CLICKED = "WidgetButtonClick";

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,  //this runs first
                         int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
    //    Log.e("aaa", " void updateAppWidget");
        views.setOnClickPendingIntent(R.id.widget_button, getPendingSelfIntent(context, SYNC_CLICKED));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (SYNC_CLICKED.equals(intent.getAction())) {
           // Log.e("aaa", "SYNC_CLICKED, onReceive");
            saveNewStamp(context);

        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) { //this runs second after updateAppWidget
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            //Log.e("aaa", " void onUpdate");
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first InstantTimestampWidget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last InstantTimestampWidget is disabled
    }

    public void saveNewStamp(Context context) {
        final String SHARED_PREFS_STAMPS = "stamps";
        final String SHARED_PREFS_MAX_ID = "maxID";
        final String SHARED_PREFS_USE_GPS = "gps";

        SharedPreferences mPrefs;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String stampsLoaded = "";
        long time = 0;
        int maxId = -1;

        boolean useGPS=false;
        String GPScoords="";

        if (mPrefs.contains(SHARED_PREFS_STAMPS))
            stampsLoaded = mPrefs.getString(SHARED_PREFS_STAMPS, "");
        if (mPrefs.contains(SHARED_PREFS_USE_GPS)) useGPS = mPrefs.getBoolean(SHARED_PREFS_USE_GPS, false);
        if (mPrefs.contains(SHARED_PREFS_MAX_ID)) maxId = mPrefs.getInt(SHARED_PREFS_MAX_ID, 0);


        Log.e("aaa", " maxId:" + maxId);
        //if (maxId >= 0) {


      //  {
            maxId++;
            Calendar c = Calendar.getInstance();
            time = c.getTimeInMillis();
           // String newstamp = ",{\"mCategoryID\":0,\"mSubtitle\":\""+context.getString(R.string.add_from_widget)+"\",\"mgpsCoordinates\":\"\",\"stampID\":" + maxId  + ",\"time\":" + Long.toString(time) + "}";

             if (useGPS) {
                 final LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                 final Location currentGeoLocation = mlocManager
                         .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                 if (currentGeoLocation==null)  Toast.makeText(context, context.getString(R.string.null_location), Toast.LENGTH_LONG).show();
                 if (currentGeoLocation!=null) GPScoords= String.valueOf(currentGeoLocation.getLatitude()) + "," +  String.valueOf(currentGeoLocation.getLongitude());

            }

        if (stampsLoaded.equals("") || stampsLoaded.equals("[]") ) {
            String newstamp = "[{\"c\":0,\"s\":\"" + context.getString(R.string.add_from_widget) + "\",\"g\":\"" + GPScoords + "\",\"i\":" + maxId + ",\"t\":" + Long.toString(time) + "}]";
            stampsLoaded = newstamp;
        }  else {
            String newstamp = ",{\"c\":0,\"s\":\"" + context.getString(R.string.add_from_widget) + "\",\"g\":\"" + GPScoords + "\",\"i\":" + maxId + ",\"t\":" + Long.toString(time) + "}";
            stampsLoaded = new StringBuilder(stampsLoaded).insert(stampsLoaded.length() - 1, newstamp).toString();
        }


        mPrefs.edit().putString(SHARED_PREFS_STAMPS, stampsLoaded).commit();
        mPrefs.edit().putInt(SHARED_PREFS_MAX_ID, maxId).commit();
        Toast.makeText(context, context.getString(R.string.new_timestamp_created), Toast.LENGTH_SHORT).show();
    }
}

