package com.petproject.ybiry.galleryonmap.data.repository;

import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.util.ArrayList;

import io.reactivex.Single;

public interface Repository {

    Single<ArrayList<Photo>> getPhotos();

    Single<ArrayList<Photo>> getCoordinates(ArrayList<Photo> photos);


}