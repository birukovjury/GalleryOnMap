package com.petproject.ybiry.galleryonmap.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Photo implements ClusterItem {

    private final String path;
    private final String title;
    private final String snipped;
    private final LatLng position;


    public Photo(String path, double latitude, double longitude, String title, String snipped) {
        this.path = path;
        this.snipped = snipped;
        this.title = title;
        position = new LatLng(latitude, longitude);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return path;
    }

    public String getPath() {
        return path;
    }

}
