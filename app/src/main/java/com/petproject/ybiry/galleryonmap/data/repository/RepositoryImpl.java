package com.petproject.ybiry.galleryonmap.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

public class RepositoryImpl implements Repository {

    private static final String CLASS_TAG = "RepositoryImpl";
    private final Context mAppContext;

    public RepositoryImpl(Context context) {
        mAppContext = context;
    }


    @Override
    public Single<List<Photo>> getPhotos() {
        return Single.create(emitter -> {
            try {
                // Thread.sleep(1000);
                List<Photo> photos = getImagePaths(mAppContext);
                if (photos != null) {
                    emitter.onSuccess(photos);
                    Log.e(CLASS_TAG, "Total count of photos =  " + photos.size() + "\n");

                } else Log.e(CLASS_TAG, "No photos");
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }


    private List<Photo> getImagePaths(Context context) {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED};
        List<Photo> photos = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String where = MediaStore.Images.Media.MIME_TYPE + "='image/jpeg'" + " OR " + MediaStore.Images.Media.MIME_TYPE + "='image/jpg'";

        final Cursor cursor = context.getContentResolver().query(uri,
                columns,
                where,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (cursor != null) {
            photos = new ArrayList<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                float[] latlng;
                final int image_path_col = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {
                    latlng = getLatLngData(cursor.getString(image_path_col));
                    if (latlng != null)
                        photos.add(new Photo(cursor.getString(image_path_col), latlng[0], latlng[1], ""));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return photos;
    }

    private float[] getLatLngData(String fullPath) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(fullPath);

        } catch (IOException e) {
            Log.e(CLASS_TAG, e.getMessage());
            return null;
        }

        float[] latlng = new float[2];
        boolean b = exif.getLatLong(latlng);
        if (b) return latlng;
        else return null;
    }
}


