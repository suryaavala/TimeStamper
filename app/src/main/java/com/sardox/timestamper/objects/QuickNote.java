package com.sardox.timestamper.objects;

import android.support.annotation.NonNull;

import com.sardox.timestamper.types.JetTimestamp;

public class QuickNote implements Comparable {

    private JetTimestamp timestamp;
    private String note;

    public QuickNote(JetTimestamp timestamp, String note) {
        this.timestamp = timestamp;
        this.note = note;
    }

    public JetTimestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(JetTimestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public int hashCode() {
        return note.toLowerCase().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuickNote quickNote = (QuickNote) o;
        return quickNote.getNote().equalsIgnoreCase(note);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return ((QuickNote) o).getTimestamp().compareTo(this.getTimestamp());
    }
}
