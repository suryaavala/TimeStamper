package com.sardox.timestamper.utils;


public class TimestampIcon {
    private int drawable_id;
    private int icon_id;

    public int getDrawable_id() {
        return drawable_id;
    }

    public void setDrawable_id(int drawable_id) {
        this.drawable_id = drawable_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;


    public TimestampIcon(int drawable_id, String description, int icon_id) {
        this.drawable_id = drawable_id;
        this.description = description;
        this.icon_id = icon_id;
    }
}
