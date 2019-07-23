package com.petproject.ybiry.galleryonmap.data.repository;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
                    Thread.sleep(1000); // makes preloader visible for a short time
                    List<Photo> photos = getImagePaths(mAppContext);
                    if (photos != null) {
                        emitter.onSuccess(photos);
                        for (int i = 0; i < photos.size(); i++) {
                            // Log.e(CLASS_TAG, "URL " + i + " = " + photos.get(i).getURL() + "\n");
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
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String where = MediaStore.Images.Media.MIME_TYPE + "='image/jpeg'" + " OR " + MediaStore.Images.Media.MIME_TYPE + "='image/jpg'";

        final Cursor cursor = context.getContentResolver().
                query(uri, // Specify the provider
                        columns, // The columns we're interested in
                        where, // A WHERE-filter query
                        null, // The arguments for the filter-query
                        MediaStore.Images.Media.DATE_ADDED + " DESC" // Order the results, newest first
                );
        if (cursor != null) {
            result = new ArrayList<Photo>(cursor.getCount());
            if (cursor.moveToFirst()) {
                final int image_path_col = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                do {
                    result.add(new Photo(cursor.getString(image_path_col), getLatLngData(cursor.getString(image_path_col))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return result;
    }

    private float[] getLatLngData(String fullPath) {
        ExifInterface exif = null;
        //  InputStream inputStream = getInputStream(fullPath);


        try {
            exif = new ExifInterface(fullPath);

          /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif = new ExifInterface(inputStream);
            } else {
                Log.e(CLASS_TAG, "Build.VERSION.SDK_INT >= Build.VERSION_CODES.N");
            }
            if (inputStream != null) {
                inputStream.close();
            } else {
                Log.e(CLASS_TAG, "inputStream = null");
            } */

        } catch (IOException e) {
            Log.e(CLASS_TAG, e.getMessage());
        }
        if (exif == null) {
            Log.e(CLASS_TAG, "exif = null");
            return null;
        } else {
            float[] latlng = new float[2];
            boolean b = exif.getLatLong(latlng);
            Log.e(CLASS_TAG, "result =  " + Arrays.toString(latlng));
            return latlng;
        }
    }

    private InputStream getInputStream(String fullPath) {

        InputStream inputStream = null;

        int index = fullPath.lastIndexOf("/");
        String fileName = fullPath.substring(index + 1);
        String path = fullPath.substring(0, fullPath.lastIndexOf('/'));

        Log.e(CLASS_TAG, fileName + " \n");
        Log.e(CLASS_TAG, path + " \n");

        File f = new File(path, fileName);
        Uri uri = Uri.fromFile(f);
        if (uri == null) Log.e(CLASS_TAG, "uri = null");
        try {
            inputStream = mAppContext.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}


