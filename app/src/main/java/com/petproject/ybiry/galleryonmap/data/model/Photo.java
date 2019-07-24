package com.petproject.ybiry.galleryonmap.data.model;

public class Photo {

    private String path;
    private double latitude = 55.0649;
    private double longitude = 82.8674;


    public Photo(String path, double latitude, double longitude) {
        this.path = path;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


}
