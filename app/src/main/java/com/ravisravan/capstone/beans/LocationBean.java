package com.ravisravan.capstone.beans;

import com.ravisravan.capstone.Constants.Constants;

import java.io.Serializable;

/**
 * Created by ravisravankumar on 17/10/16.
 */
public class LocationBean implements Serializable {

    private double latitude;
    private double longitude;
    private double radiusInMeters;
    private String displayAddress;
    private boolean isInitialised;

    private int frequency = Constants.LOCATION_FREQUENCY_ONCE;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadiusInMeters() {
        return radiusInMeters;
    }

    public void setRadiusInMeters(double radiusInMeters) {
        this.radiusInMeters = radiusInMeters;
    }

    public String getDisplayAddress() {
        return displayAddress;
    }

    public void setDisplayAddress(String displayAddress) {
        this.displayAddress = displayAddress;
    }

    public boolean isInitialised() {
        return isInitialised;
    }

    public void setInitialised(boolean initialised) {
        isInitialised = initialised;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
