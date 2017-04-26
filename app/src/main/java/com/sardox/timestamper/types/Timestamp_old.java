package com.sardox.timestamper.types;


public class Timestamp_old {
    private String n;  //mName
    private String s; //mSubtitle
    private String g; //mgpsCoordinates
    private int c; //mCategoryID
    private int i; //stampID
    private long t; //time

    public Timestamp_old(String name, String subtitle, String gpsCoordinates, int categoryID, long time) {
        this.n = name;
        this.s = subtitle;
        this.g = gpsCoordinates;
        this.c = categoryID;
        this.t = t;
    }

    public Timestamp_old() {
    }

    public String getNote() {
        return s;
    }

    public JetTimestamp getJetTime() {
        return JetTimestamp.fromMilliseconds(t);
    }
}
