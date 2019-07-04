package com.petproject.ybiry.galleryonmap.data.model;

public class Photo {

    private String path;
    private double latitude = 55.0649;
    private double longitude = 82.8674;

    public Photo(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Photo(String path, double latitude, double longitude) {
        this.path = path;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getURL() {
        return path;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setURL(String path) {
        this.path = path;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
