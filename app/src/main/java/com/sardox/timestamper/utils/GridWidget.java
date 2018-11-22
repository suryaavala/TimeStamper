package com.sardox.timestamper.utils;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sardox.timestamper.AppInstance;
import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;

import java.util.HashMap;


public class GridWidget extends AppWidgetProvider {
    public static final String EXTRA_ITEM = "com.sardox.timestamper.grid_item_id";
    public static final String CATEGORY_TO_ADD = "com.sardox.timestamper.category_to_add";
    private static final String ADD_TIMESTAMP = "com.sardox.timestamper.add_new_timestamp";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ADD_TIMESTAMP)) {
            String categoryId = intent.getStringExtra(CATEGORY_TO_ADD);
            saveNewStamp(context, categoryId);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) { //this runs second after updateAppWidget
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_layout);
            views.setRemoteAdapter(R.id.gridview, intent);

            Intent addTimestampIntent = new Intent(context, GridWidget.class);
            addTimestampIntent.setAction(GridWidget.ADD_TIMESTAMP);
            addTimestampIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, addTimestampIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.gridview, toastPendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    public void saveNewStamp(final Context context, String categoryForWidget) {
        Log.i("MainActivity", "saveNewStamp");

        JetTimestamp instantTimestamp = JetTimestamp.now();
        logEvent(Constants.Analytics.Events.GRID_WIDGET_CLICK);

        DataManager dataManager = new DataManager(context);
        AppSettings appSettings = dataManager.loadUserSettings();
        final Timestamp timestamp = new Timestamp(instantTimestamp, PhysicalLocation.Default, JetUUID.fromString(categoryForWidget), "added from widget", JetUUID.randomUUID());

        if (appSettings.getShouldUseGps() && hasGPSpermission(context)) {
            logEvent(Constants.Analytics.Events.GRID_WIDGET_GPS_ATTEMPT);
            try {
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
                mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location lastKnownLocation) {
                        if (lastKnownLocation == null) {
                            logEvent(Constants.Analytics.Events.GRID_WIDGET_GPS_FAILED);
                            saveTimestamp(context, null, timestamp);
                        } else {
                            logEvent(Constants.Analytics.Events.GRID_WIDGET_GPS_SUCCESS);
                            PhysicalLocation physicalLocation = new PhysicalLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            saveTimestamp(context, physicalLocation, timestamp);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        logEvent(Constants.Analytics.Events.GRID_WIDGET_GPS_FAILED);
                        saveTimestamp(context, null, timestamp);
                    }
                });
            } catch (SecurityException e) {
                Log.e("SecurityException", "SecurityException during getLastLocation in widget");
            }
        } else
            logEvent(Constants.Analytics.Events.GRID_WIDGET_NO_GPS_SUCCESS);
            saveTimestamp(context, PhysicalLocation.Default, timestamp);
    }

    private void saveTimestamp(Context context, PhysicalLocation physicalLocation, Timestamp timestamp) {
        DataManager dataManager = new DataManager(context);
        HashMap<JetUUID, Timestamp> widgetTimestamps = dataManager.readWidgetTimestamps();
        if (physicalLocation == null) {
            timestamp.setPhysicalLocation(PhysicalLocation.Default);
            Toast.makeText(context, context.getString(R.string.null_location), Toast.LENGTH_SHORT).show();
        } else {
            timestamp.setPhysicalLocation(physicalLocation);
            if (physicalLocation.equals(PhysicalLocation.Default)) {
                Toast.makeText(context, context.getString(R.string.new_timestamp_created), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, context.getString(R.string.new_timestamp_created_with_location), Toast.LENGTH_SHORT).show();
            }
        }
        widgetTimestamps.put(timestamp.getIdentifier(), timestamp);
        dataManager.writeWidgetTimestamps(widgetTimestamps);
    }

    private boolean hasGPSpermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void logEvent(String event) {
        AppInstance.firebaseAnalytics.logEvent(event, new Bundle());
    }
}
