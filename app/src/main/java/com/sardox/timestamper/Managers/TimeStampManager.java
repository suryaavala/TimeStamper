package com.sardox.timestamper.Managers;


import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.PhysicalLocation;


public class TimeStampManager {
    private boolean isGPSon = false;

    public TimeStampManager() {

    }

    public Timestamp createTimestamp(Category category) {
        return new Timestamp(JetTimestamp.now(), getLocation(), category.getCategoryID());
    }

    public Timestamp createTimestamp(int categoryID) {
        return new Timestamp(JetTimestamp.now(), getLocation(), categoryID);
    }

    public Timestamp createTimestamp(int categoryID, String note) {
        return new Timestamp(JetTimestamp.now(), getLocation(), categoryID, note);
    }


    private PhysicalLocation getLocation() {
        return PhysicalLocation.Default;
    }

    public void enableGPS() {
        isGPSon = true;
    }

    public void disableGPS() {
        isGPSon = false;
    }

}
