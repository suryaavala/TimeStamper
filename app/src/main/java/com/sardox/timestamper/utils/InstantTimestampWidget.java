package com.sardox.timestamper.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sardox.timestamper.AppInstance;
import com.sardox.timestamper.DialogActivity;
import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;

import java.util.HashMap;

public class InstantTimestampWidget extends AppWidgetProvider {

    private static final String ADD_TIMESTAMP = "AddNewTimestamp";

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void showSetupDialog(Context context) {
        Intent myIntent = new Intent(context, DialogActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myIntent);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,  //this runs first
                                 int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setOnClickPendingIntent(R.id.widget_button, getPendingSelfIntent(context, ADD_TIMESTAMP));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ADD_TIMESTAMP.equals(intent.getAction())) {
            DataManager dataManager = new DataManager(context);
            JetUUID defaultCategoryForWidget = dataManager.readDefaultCategoryForWidget();
            if (defaultCategoryForWidget.equals(AppSettings.Companion.getNO_DEFAULT_CATEGORY())) {
                showSetupDialog(context);
            } else {
                saveNewStamp(context, defaultCategoryForWidget);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) { //this runs second after updateAppWidget
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("srdx", "First widget was created");
        // Enter relevant functionality for when the first InstantTimestampWidget is created
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d("srdx", "Last widget was deleted");
        DataManager dataManager = new DataManager(context);
        dataManager.saveDefaultCategoryForWidget(AppSettings.Companion.getNO_DEFAULT_CATEGORY());
    }

    public void saveNewStamp(Context context, JetUUID defaultCategoryForWidget) {
        logEvent(Constants.Analytics.Events.WIDGET_CLICK);

        DataManager dataManager = new DataManager(context);
        AppSettings appSettings = dataManager.loadUserSettings();

        PhysicalLocation physicalLocation = PhysicalLocation.Default;

        if (appSettings.getShouldUseGps() && hasGPSpermission(context)) {
            final LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            Location lastKnownLocation = null;
            if (mlocManager != null) {
                try {
                    lastKnownLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                } catch (SecurityException e) {
                    Log.e("SecurityException", "SecurityException during getLastKnownLocation in widget");
                }
            }

            if (lastKnownLocation == null) {
                Toast.makeText(context, context.getString(R.string.null_location), Toast.LENGTH_SHORT).show();
            } else {
                physicalLocation = new PhysicalLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }
        }

        HashMap<JetUUID, Timestamp> widgetTimestamps = dataManager.readWidgetTimestamps();
        Timestamp timestamp = new Timestamp(JetTimestamp.now(), physicalLocation, defaultCategoryForWidget, "added from widget", JetUUID.randomUUID());
        widgetTimestamps.put(timestamp.getIdentifier(), timestamp);
        dataManager.writeWidgetTimestamps(widgetTimestamps);

        Toast.makeText(context, context.getString(R.string.new_timestamp_created), Toast.LENGTH_SHORT).show();
    }

    private boolean hasGPSpermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void logEvent(String event) {
        AppInstance.firebaseAnalytics.logEvent(event, new Bundle());
    }
}
