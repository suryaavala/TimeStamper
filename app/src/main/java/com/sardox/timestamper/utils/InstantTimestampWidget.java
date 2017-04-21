package com.sardox.timestamper.utils;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.sardox.timestamper.Managers.DataManager;
import com.sardox.timestamper.R;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class InstantTimestampWidget extends AppWidgetProvider {

    private static final String ADD_TIMESTAMP = "AddNewTimestamp";

    private PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
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
            saveNewStamp(context);
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
        // Enter relevant functionality for when the first InstantTimestampWidget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last InstantTimestampWidget is disabled
    }

    public void saveNewStamp(Context context) {
        DataManager dataManager = new DataManager(context);
        AppSettings appSettings = dataManager.readSettings();

        PhysicalLocation physicalLocation = PhysicalLocation.Default;

        if (appSettings.isUse_gps()) {
            final LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            final Location currentGeoLocation = mlocManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (currentGeoLocation == null)
                Toast.makeText(context, context.getString(R.string.null_location), Toast.LENGTH_LONG).show();
            if (currentGeoLocation != null)
                physicalLocation = new PhysicalLocation(currentGeoLocation.getLatitude(), currentGeoLocation.getLongitude());
        }

        HashMap<JetUUID, Timestamp> widgetTimestamps = dataManager.readWidgetTimestamps();
        Timestamp timestamp = new Timestamp(JetTimestamp.now(), physicalLocation, JetUUID.Zero, "added from widget", JetUUID.randomUUID());
        widgetTimestamps.put(timestamp.getIdentifier(), timestamp);
        dataManager.writeWidgetTimestamps(widgetTimestamps);

        Toast.makeText(context, context.getString(R.string.new_timestamp_created), Toast.LENGTH_SHORT).show();
    }
}
