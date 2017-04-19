package com.sardox.timestamper.objects;




public class Category {
    private String n;
    private int c;

    public static final Category Default = new Category();

    public Category() {
        this.n = "Default";
        this.c = 0;
        this.icon_id=0;
    }

    public Category(String name, int mCategoryID, int icon_id) {
        this.n = name;
        this.c = mCategoryID;
        this.icon_id = icon_id;
    }


    public int getIcon_id() {
        return icon_id;
    }

    private int icon_id=0;

    public String getName() {
        return n;
    }

    public void setName(String name) {
        this.n = name;
    }

    public int getCategoryID() {
        return c;
    }

    public void setCategoryID(int mCategoryID) {
        this.c = mCategoryID;
    }


}









