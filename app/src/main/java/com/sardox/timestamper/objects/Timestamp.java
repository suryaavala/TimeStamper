package com.sardox.timestamper.objects;

import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;
import com.sardox.timestamper.types.TimestampFormat;

import java.util.Calendar;

public class Timestamp {

    private JetUUID category_identifier;
    private JetUUID identifier;
    private Calendar calendar;
    private JetTimestamp timestamp;
    private PhysicalLocation physicalLocation;
    private String note;

    public Timestamp(JetTimestamp now, PhysicalLocation location, JetUUID category_identifier, String note, JetUUID identifier) {
        this.timestamp = now;
        this.physicalLocation = location;
        this.category_identifier = category_identifier;
        this.note = note;
        this.identifier=identifier;
    }

    public Timestamp(JetTimestamp now, PhysicalLocation location, JetUUID category_identifier, JetUUID identifier) {
        this.timestamp = now;
        this.physicalLocation = location;
        this.category_identifier = category_identifier;
        this.note = "";
        this.identifier=identifier;
    }

    public int format(TimestampFormat format) {
        if (calendar == null) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp.toMilliseconds());
        }
        switch (format) {
            case Day:
                return calendar.get(Calendar.DAY_OF_MONTH);
            case Month:
                return calendar.get(Calendar.MONTH);  //Months are indexed from 0 to 11
            case Year:
                return calendar.get(Calendar.YEAR);
            case HRS12:
                return calendar.get(Calendar.HOUR);
            case HRS24:
                return calendar.get(Calendar.HOUR_OF_DAY);
            case MIN:
                return calendar.get(Calendar.MINUTE);
            case SEC:
                return calendar.get(Calendar.SECOND);
            case MS:
                return calendar.get(Calendar.MILLISECOND);
        }
        return 999999;
    }

    public JetTimestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(JetTimestamp timestamp) {
        this.timestamp = timestamp;
        calendar = null;
    }

    public String getNote() {
        return note;
    }

    public JetUUID getIdentifier() {
        return identifier;
    }

    public PhysicalLocation getPhysicalLocation() {
        return physicalLocation;
    }

    public void setPhysicalLocation(PhysicalLocation physicalLocation) {
        this.physicalLocation = physicalLocation;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public JetUUID getCategoryId() {
        return category_identifier;
    }

    public void setCategory_identifier(JetUUID category_identifier) {
        this.category_identifier = category_identifier;
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + physicalLocation.hashCode();
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + category_identifier.hashCode();
        result = 31 * result + identifier.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Timestamp timestamp1 = (Timestamp) o;

        if (!timestamp.equals(timestamp1.timestamp)) return false;
        if (!physicalLocation.equals(timestamp1.physicalLocation)) return false;
        if (note != null ? !note.equals(timestamp1.note) : timestamp1.note != null) return false;
        if (!category_identifier.equals(timestamp1.category_identifier)) return false;
        return identifier.equals(timestamp1.identifier);

    }
}
