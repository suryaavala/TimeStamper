package com.sardox.timestamper.utils;


import java.util.List;

public class AppSettings {

    private boolean use24hrFormat = false;            //default values
    private boolean useDark = true;                   //default values
    private boolean showNoteAddDialog = false;        //default values
    private boolean showMillis = false;               //default values


    public AppSettings() {
    }

    public boolean isUse24hrFormat() {
        return use24hrFormat;
    }

    public void setUse24hrFormat(boolean use24hrFormat) {
        this.use24hrFormat = use24hrFormat;
    }

    public boolean isUseDark() {
        return useDark;
    }

    public void setUseDark(boolean useDark) {
        this.useDark = useDark;
    }

    public boolean isShowNoteAddDialog() {
        return showNoteAddDialog;
    }

    public void setShowNoteAddDialog(boolean showNoteAddDialog) {
        this.showNoteAddDialog = showNoteAddDialog;
    }

    public boolean isShowMillis() {
        return showMillis;
    }

    public void setShowMillis(boolean showMillis) {
        this.showMillis = showMillis;
    }

    public boolean isUse_gps() {
        return use_gps;
    }

    public void setUse_gps(boolean use_gps) {
        this.use_gps = use_gps;
    }

    public boolean use_gps = false;                  //default values
}
