package com.sardox.timestamper.utils;

import android.Manifest;

public class Constants {
    public static final String[] STORAGE_PERMS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static final String EXPORT_NAME = "My timestamps";
    public static final String EXPORT_FILE_NAME = "MyTimestamps.csv";
    public static final String EXPORT_FILE_TYPE = "text/html";
    public static final int GPS_REQUEST_TIMEOUT = 1000 * 20;

    public class Analytics {

        public class Events {
            public static final String ACTION = "Action";
            public static final String NEW_TIMESTAMP = "New timestamp";
            public static final String APP_LAUNCH = "App launch";
            public static final String TIMESTAMP_REMOVE = "Timestamp removed";
            public static final String OPEN_MAP = "Open Map";
            public static final String ADD_NEW_CATEGORY = "Add new category";
            public static final String NEW_CATEGORY = "New category";
            public static final String REMOVE_CATEGORY = "Remove category";
            public static final String EXPORT_TIMESTAMPS = "Export timestamps";
            public static final String LOCATION_RECORDED = "Location was recorded";
            public static final String EDIT_DATE = "Edit date";
            public static final String EDIT_TIME = "Edit time";
            public static final String EDIT_CATEGORY = "Edit category";
            public static final String WIDGET_CLICK = "Widget click";
            public static final String ICON_PICKED = "Icon picked";
        }

        public class Screens {
            public static final String MainScreen = "Main screen";
        }
    }

    public class Settings {
        public static final String SHARED_PREFS_SHOW_MILLIS = "SHOW_MILLIS";
        public static final String SHARED_PREFS_AUTONOTE = "AUTO_NOTE";
        public static final String SHARED_PREFS_TIMESTAMPS = "TIMESTAMPS";
        public static final String SHARED_PREFS_WIDGET_TIMESTAMPS = "TIMESTAMPS_WIDGET";
        public static final String SHARED_PREFS_USE24HR = "USE_24HR";
        public static final String SHARED_PREFS_USEDARK = "USE_DARK";
        public static final String SHARED_PREFS_CATEGORIES = "CATEGORIES";
        public static final String SHARED_PREFS_USE_GPS = "USE_GPS";
        public static final String SHARED_PREFS_WIDGET_DEFAULT_TIMESTAMP = "WIDGET_CATEGORY";
    }

}