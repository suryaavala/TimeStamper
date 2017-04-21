package com.sardox.timestamper.types;

public final class PhysicalLocation {
    private final double latitude;
    private final double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhysicalLocation that = (PhysicalLocation) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        return Double.compare(that.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static final PhysicalLocation Default = new PhysicalLocation();


    public static PhysicalLocation fromLatLon(double latitude, double longitude) {
        return new PhysicalLocation(latitude, longitude);
    }

    public PhysicalLocation() {
        this.latitude = -360;
        this.longitude = -360;
    }

    public PhysicalLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "PhysicalLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public String toSimpleCommaString() {
        return latitude + "," + longitude;
    }
}
