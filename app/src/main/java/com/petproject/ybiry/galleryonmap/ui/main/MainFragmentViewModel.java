package com.petproject.ybiry.galleryonmap.ui.main;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.petproject.ybiry.galleryonmap.arch.BaseViewModel;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.data.repository.Repository;
import com.petproject.ybiry.galleryonmap.data.repository.RepositoryImpl;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainFragmentViewModel extends BaseViewModel implements LifecycleObserver {

    private static final String CLASS_TAG = "MainFrViewModel";

    private MutableLiveData<List<Photo>> mListOfPhotosLiveData;
    private MutableLiveData<String> mToastLiveData;
    private MutableLiveData<String[]> mRequestPermissionLiveData;

    private Repository mRepo;

    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
    }

    void init() {
        if (mListOfPhotosLiveData == null)
            mListOfPhotosLiveData = new MutableLiveData<>();

        if (mToastLiveData == null)
            mToastLiveData = new MutableLiveData<>();

        if (mRequestPermissionLiveData == null)
            mRequestPermissionLiveData = new MutableLiveData<>();

        if (mRepo == null)
            mRepo = new RepositoryImpl(getApplication().getApplicationContext());

        askViewForPermissions();
    }

    private void askViewForPermissions() {
        if (!isExternalStorageGranted(getApplication().getApplicationContext()))
            requestExternalStorageReadPermissions();
    }


    void getInitialData() {
        mRepo.getPhotos().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> postLoading(true))
                .doFinally(() -> setLoading(false))
                .subscribe(new SingleObserver<List<Photo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<Photo> photos) {
                        Log.v(CLASS_TAG, "GetPhotos onSuccess");
                        mListOfPhotosLiveData.setValue(photos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(CLASS_TAG, "GetPhotos onError");
                        mListOfPhotosLiveData.setValue(null);
                    }
                });
    }


    private void requestExternalStorageReadPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
        mRequestPermissionLiveData.postValue(permissions);
    }


    private boolean isExternalStorageGranted(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    LiveData<List<Photo>> getPhotos() {
        return mListOfPhotosLiveData;
    }

    LiveData<String> getToast() {
        return mToastLiveData;
    }

    LiveData<String[]> getRequestPermissions() {
        return mRequestPermissionLiveData;
    }
}
