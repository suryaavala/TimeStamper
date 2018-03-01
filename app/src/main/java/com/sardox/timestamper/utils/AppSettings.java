package com.sardox.timestamper.utils;

import com.sardox.timestamper.objects.QuickNote;
import com.sardox.timestamper.objects.QuickNoteList;
import com.sardox.timestamper.types.JetUUID;

public class AppSettings {

    public static final JetUUID NO_DEFAULT_CATEGORY = JetUUID.fromString("3f02dce5-d2de-4d3c-96d6-de0f6123baa8");

    private boolean use24hrFormat = false;
    private boolean useDark = true;
    private boolean showNoteAddDialog = false;
    private boolean shouldUseGps = false;
    private boolean shouldUseQuickNotes = true;
    private boolean shouldShowMillis = false;
    private boolean shouldShowKeyboardInAddNote = false;
    private QuickNoteList quickNotes;

    public AppSettings() {
        this.quickNotes = new QuickNoteList();
    }

    public QuickNoteList getQuickNotes() {
        return quickNotes;
    }

    public void setQuickNotes(QuickNoteList quickNotes) {
        this.quickNotes = quickNotes;
    }

    public void addQuickNote(QuickNote quickNote) {
        this.quickNotes.addNote(quickNote);
    }

    public boolean shouldUse24hrFormat() {
        return use24hrFormat;
    }

    public void setUse24hrFormat(boolean use24hrFormat) {
        this.use24hrFormat = use24hrFormat;
    }

    public boolean shouldUseDarkTheme() {
        return useDark;
    }

    public void setUseDark(boolean useDark) {
        this.useDark = useDark;
    }

    public boolean shouldShowNoteAddDialog() {
        return showNoteAddDialog;
    }

    public void setShowNoteAddDialog(boolean showNoteAddDialog) {
        this.showNoteAddDialog = showNoteAddDialog;
    }

    public boolean shouldShowMillis() {
        return shouldShowMillis;
    }

    public void setShowMillis(boolean showMillis) {
        this.shouldShowMillis = showMillis;
    }

    public boolean shouldUseGps() {
        return shouldUseGps;
    }

    public void setShouldUseGps(boolean flag) {
        this.shouldUseGps = flag;
    }

    public boolean shouldUseQuickNotes() {
        return shouldUseQuickNotes;
    }

    public void setShouldUseQuickNotes(boolean flag) {
        this.shouldUseQuickNotes = flag;
    }

    public boolean shouldShowKeyboardInAddNote() {
        return shouldShowKeyboardInAddNote;
    }

    public void setShouldShowKeyboardInAddNote(boolean shouldShowKeyboardInAddNote) {
        this.shouldShowKeyboardInAddNote = shouldShowKeyboardInAddNote;
    }
}
