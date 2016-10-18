package com.sardox.timestamper.objects;


/**
 * Created by sardox on 7/23/2016.
 */
public class Timestamp {
    private String n;  //mName
    private String s; //mSubtitle
    private String g; //mgpsCoordinates

    private int c; //mCategoryID
    private int i; //stampID
    private long t; //time


    public Timestamp() {
    }

    public Timestamp(String name, String subtitle, String gpsCoordinates, int categoryID, long time) {
        this.n = name;
        this.s = subtitle;
        this.g = gpsCoordinates;
        this.c = categoryID;
        this.t = t;
    }

    public String getName() {
        return n;
    }


    public void setName(String name) {
        this.n = name;
    }

    public String getSubtitle() {
        return s;
    }

    public void setSubtitle(String subtitle) {
        this.s = subtitle;
    }




    public void setGps(String gps) {
        this.g = gps;
    }

    public String getGps() {
        return g;
    }



    public int getCategoryID() {
        return c;
    }


    public void setCategoryID(int category) {
        this.c = category;
    }

    public long getTime() {
        return t;
    }


    public void setTime(long time) {
        this.t = time;
    }

    public int getStampID() {
        return i;
    }


    public void setStampID(int id) {
        this.i = id;
    }
}









