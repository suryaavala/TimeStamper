package com.sardox.timestamper.objects;


import com.sardox.timestamper.types.JetUUID;

public class Category {
    private String name;
    private JetUUID identifier;

    public static final Category Default = new Category();

    public Category() {
        this.name = "Default";
        this.identifier = JetUUID.Zero;
        this.icon_id=0;
    }

    public Category(String name, JetUUID identifier, int icon_id) {
        this.name = name;
        this.identifier = identifier;
        this.icon_id = icon_id;
    }


    public int getIcon_id() {
        return icon_id;
    }

    private int icon_id=0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JetUUID getCategoryID() {
        return identifier;
    }

    public void setCategoryID(JetUUID identifier) {
        this.identifier = identifier;
    }


}









