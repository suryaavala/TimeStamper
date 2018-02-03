package com.sardox.timestamper.objects;


import com.sardox.timestamper.types.JetUUID;

public class Category {
    private String name;
    private JetUUID identifier;

    public static final Category Default = new Category();

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + identifier.hashCode();
        result = 31 * result + icon_id;
        return result;
    }

    private Category() {
        this.name = "DEFAULT";
        this.identifier = JetUUID.Zero;
        this.icon_id = 0;
    }

    public Category(String name, JetUUID identifier, int icon_id) {
        this.name = name;
        this.identifier = identifier;
        this.icon_id = icon_id;
    }

    public int getIcon_id() {
        return icon_id;
    }


    private int icon_id = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JetUUID getCategoryID() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (icon_id != category.icon_id) return false;
        if (!name.equals(category.name)) return false;
        return identifier.equals(category.identifier);
    }

}
