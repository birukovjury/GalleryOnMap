package com.petproject.ybiry.galleryonmap.data.repository;

import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.util.List;

import io.reactivex.Single;

public interface Repository {

    Single<List<Photo>> getPhotos();

}