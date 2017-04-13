package com.sardox.timestamper.objects;




public class Category {
    private String n;
    private int c;

    public static final Category Default = new Category();


    public Category() {
        this.n = "Default";
        this.c = 0;
    }

    public Category(String name, int mCategoryID) {
        this.n = name;
        this.c = mCategoryID;
    }

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









