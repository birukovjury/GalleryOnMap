package com.petproject.ybiry.galleryonmap.data.model;

public class Photo {

    private String path;
    private String latitude;
    private String longitude;

    public String getURL() {
        return path;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setURL(String path) {
        this.path = path;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
