package com.petproject.ybiry.galleryonmap.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class Photo implements ClusterItem {

    private String path;
    private final LatLng position;


    public Photo(String path, double latitude, double longitude) {
        this.path = path;
        position = new LatLng(latitude, longitude);
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
