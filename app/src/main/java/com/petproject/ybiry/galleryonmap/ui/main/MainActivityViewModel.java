package com.petproject.ybiry.galleryonmap.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleObserver;

import com.petproject.ybiry.galleryonmap.arch.BaseViewModel;

public class MainActivityViewModel extends BaseViewModel implements LifecycleObserver {
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }
}
