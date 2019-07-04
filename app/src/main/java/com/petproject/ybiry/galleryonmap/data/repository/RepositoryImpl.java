package com.petproject.ybiry.galleryonmap.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class RepositoryImpl implements Repository {

    private static final String CLASS_TAG = "RepositoryImpl";
    private final Context mAppContext;

    public RepositoryImpl(Context context) {
        mAppContext = context;
    }


    @Override
    public Single<List<Photo>> getPhotos() {
        return Single.create(new SingleOnSubscribe<List<Photo>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Photo>> emitter) throws Exception {
                try {
                    Thread.sleep(1000);
                    List<Photo> photos = getImagePaths(mAppContext);
                    if (photos != null) {
                        emitter.onSuccess(photos);
                        for (int i = 0; i < photos.size(); i++) {
                            Log.e(CLASS_TAG, "URL " + i + " = " + photos.get(i).getURL() + "\n");
                        }
                    } else Log.e(CLASS_TAG, "No photos");
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    @Override
    public Single<List<Photo>> getCoordinates(ArrayList<Photo> paths) {
        return Single.create(new SingleOnSubscribe<List<Photo>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Photo>> emitter) throws Exception {
                //TODO
                emitter.onSuccess(new ArrayList<Photo>());
                //   emitter.onError(new Exception("lol"));
            }
        });
    }

    private List<Photo> getImagePaths(Context context) {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED};
        List<Photo> result = null;

        final Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // Specify the provider
                        columns, // The columns we're interested in
                        null, // A WHERE-filter query
                        null, // The arguments for the filter-query
                        MediaStore.Images.Media.DATE_ADDED + " DESC" // Order the results, newest first
                );

        if (cursor != null) {
            result = new ArrayList<Photo>(cursor.getCount());
            if (cursor.moveToFirst()) {
                final int image_path_col = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {
                    result.add(new Photo(cursor.getString(image_path_col), 55.0649, 82.8674));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return result;
    }
}
