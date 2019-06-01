package com.deliverycircuit.ehtp.myapplication;

public class LocationItem {
    private String locationName;

    private String locationDescription;

    private double[] locationPosition;

    public LocationItem(String locationName, String locationDescription, double[] locationPosition) {
        this.locationName = locationName;
        this.locationDescription = locationDescription;
        this.locationPosition = locationPosition;
    }

    public double[] getLocationPosition() {
        return locationPosition;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getLocationName()
    {
        return locationName;
    }
}
