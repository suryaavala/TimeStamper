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
            public static final String SHOW_SETTINGS = "pageview_settings";
            public static final String SHOW_MAIN_ON_CREATE = "pageview_main_on_create";
            public static final String SHOW_MAIN_ON_RESUME = "pageview_main_on_resume";
            public static final String SHOW_MAIN_ON_START = "pageview_main_on_start";
            public static final String SETTINGS_LOADED = "settings_loaded";
            public static final String NEW_TIMESTAMP = "timestamp_new";
            public static final String REMOVE_GROUP = "timestamp_remove_group";
            public static final String REMOVE_GROUP_UNDO = "timestamp_remove_group_cancel";
            public static final String EDIT_NOTE = "timestamp_edit_note";
            public static final String TIMESTAMP_REMOVE = "timestamp_remove";
            public static final String OPEN_MAP = "pageview_map";
            public static final String ADD_NEW_CATEGORY = "category_add_begin";
            public static final String NEW_CATEGORY = "category_add_success";
            public static final String TOTAL_CATEGORIES = "category_total";
            public static final String TOTAL_TIMESTAMPS = "timestamp_total";
            public static final String REMOVE_CATEGORY = "category_remove_success";
            public static final String EXPORT_TIMESTAMPS = "timestamp_export";
            public static final String EDIT_DATE = "timestamp_edit_date";
            public static final String EDIT_TIME = "timestamp_edit_time";
            public static final String EDIT_CATEGORY = "timestamp_edit_category";
            public static final String WIDGET_CLICK = "widget_click";
            public static final String GRID_WIDGET_CLICK = "widget_grid_click";
            public static final String GRID_WIDGET_GPS_ATTEMPT = "widget_grid_gps_attempt";
            public static final String GRID_WIDGET_NO_GPS_SUCCESS = "widget_grid_gpsoff_success";
            public static final String GRID_WIDGET_GPS_SUCCESS = "widget_grid_gpson_success";
            public static final String GRID_WIDGET_GPS_FAILED = "widget_grid_gpson_fail";

            //public static final String ICON_PICKED = "Icon picked";
            public static final String SECURITY_EXCEPTION = "SecurityExceptionInApp";
        }
    }

    public class Settings {
        public static final String SHARED_PREFS_SHOW_MILLIS = "SHOW_MILLIS";
        public static final String SHARED_PREFS_AUTONOTE = "AUTO_NOTE";
        public static final String SHARED_PREFS_TIMESTAMPS = "TIMESTAMPS";
        public static final String SHARED_PREFS_QUICK_NOTES = "NOTES";
        public static final String SHARED_PREFS_WIDGET_TIMESTAMPS = "TIMESTAMPS_WIDGET";
        public static final String SHARED_PREFS_USE24HR = "USE_24HR";
        public static final String SHARED_PREFS_USE_QUICK_NOTES = "USE_QUICK_NOTES";
        public static final String SHARED_PREFS_SHOW_KEYBOARD = "SHOW_KEYBOARD_IN_NOTES";
        public static final String SHARED_PREFS_CATEGORIES = "CATEGORIES";
        public static final String SHARED_PREFS_USE_GPS = "USE_GPS";
        public static final String SHARED_PREFS_WIDGET_DEFAULT_TIMESTAMP = "WIDGET_CATEGORY";
    }

}