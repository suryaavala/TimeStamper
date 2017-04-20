package com.sardox.timestamper.Managers;


import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;


public class TimeStampManager {
    private boolean isGPSon = false;

    public TimeStampManager() {

    }

    public Timestamp createTimestamp(Category category) {
        return new Timestamp(JetTimestamp.now(), getLocation(), category.getCategoryID(), JetUUID.randomUUID());
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
