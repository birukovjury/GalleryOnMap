package com.petproject.ybiry.galleryonmap.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;

import com.petproject.ybiry.galleryonmap.arch.BaseViewModel;
import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.util.List;

public class MainActivityViewModel extends BaseViewModel implements LifecycleObserver {

    private static final String CLASS_TAG = "MainFrViewModel";

    private MutableLiveData<List<Photo>> mListOfPhotosLiveData;
    private MutableLiveData<String> mToastLiveData;
    private MutableLiveData<String[]> mRequestPermissionLiveData;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }
}
