package com.petproject.ybiry.galleryonmap.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RepositoryImpl implements Repository {

    private static final String CLASS_TAG = "RepositoryImpl";

    private final Context mAppContext;

    public RepositoryImpl(Context context) {
        mAppContext = context;
    }


    @Override
    public Single<ArrayList<Photo>> getPhotos() {
        return Single.create(new SingleOnSubscribe<ArrayList<Photo>>() {
            @Override
            public void subscribe(final SingleEmitter<ArrayList<Photo>> emitter) throws Exception {
                //TODO
                

                Thread.sleep(3000);
                ArrayList<Photo> photos = new ArrayList<Photo>();
                photos.add(new Photo(55.0649, 82.8674));
                photos.add(new Photo(51.0649, 81.8674));
                photos.add(new Photo(45.0649, 33.8674));
                emitter.onSuccess(photos);
                //  emitter.onError(new Exception("lol"));
            }
        });
    }

    @Override
    public Single<ArrayList<Photo>> getCoordinates(ArrayList<Photo> paths) {
        return Single.create(new SingleOnSubscribe<ArrayList<Photo>>() {
            @Override
            public void subscribe(final SingleEmitter<ArrayList<Photo>> emitter) throws Exception {
                //TODO
                emitter.onSuccess(new ArrayList<Photo>());
                //   emitter.onError(new Exception("lol"));
            }
        });
    }
}
