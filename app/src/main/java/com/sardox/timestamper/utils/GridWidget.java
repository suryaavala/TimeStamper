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
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sardox.timestamper.Application;
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

    public void saveNewStamp(Context context, String categoryForWidget) {
        JetTimestamp instantTimestamp = JetTimestamp.now();
        Tracker mTracker = ((Application) context.getApplicationContext()).getDefaultTracker();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(Constants.Analytics.Events.ACTION)
                .setAction(Constants.Analytics.Events.GRID_WIDGET_CLICK)
                .build());

        DataManager dataManager = new DataManager(context);
        AppSettings appSettings = dataManager.loadUserSettings();

        PhysicalLocation physicalLocation = PhysicalLocation.Default;

        if (appSettings.shouldUseGps() && hasGPSpermission(context)) {
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
        Timestamp timestamp = new Timestamp(instantTimestamp, physicalLocation, JetUUID.fromString(categoryForWidget), "added from widget", JetUUID.randomUUID());
        widgetTimestamps.put(timestamp.getIdentifier(), timestamp);
        dataManager.writeWidgetTimestamps(widgetTimestamps);

        Toast.makeText(context, context.getString(R.string.new_timestamp_created), Toast.LENGTH_SHORT).show();
    }

    private boolean hasGPSpermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
