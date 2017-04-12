package com.sardox.timestamper.objects;




import com.sardox.timestamper.types.JetTimestamp;
import com.sardox.timestamper.types.PhysicalLocation;


public class Timestamp {

    private JetTimestamp timestamp;

    private PhysicalLocation physicalLocation;
    private String note;
    private int categoryId;
    public Timestamp(JetTimestamp now, PhysicalLocation location, int categoryID, String note) {
        this.timestamp=now;
        this.physicalLocation=location;
        this.categoryId=categoryID;
        this.note=note;
    }

    public Timestamp(JetTimestamp now, PhysicalLocation location, int categoryID) {
        this.timestamp=now;
        this.physicalLocation=location;
        this.categoryId=categoryID;
        this.note="";
    }

    public JetTimestamp getTimestamp() {
        return timestamp;
    }

    public PhysicalLocation getPhysicalLocation() {
        return physicalLocation;
    }

    public String getNote() {
        return note;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
