package com.sardox.timestamper.Managers;


import com.sardox.timestamper.objects.Category;
import com.sardox.timestamper.objects.Timestamp;
import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.JetUUID;
import com.sardox.timestamper.types.PhysicalLocation;

public class TimeStampManager {


    public TimeStampManager(){
    }

    public Timestamp createTimestamp(Category category) {
        return new Timestamp(JetTimestamp.now(), PhysicalLocation.Default, category.getCategoryID(), JetUUID.randomUUID());
    }

}
