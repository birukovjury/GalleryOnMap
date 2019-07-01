package com.petproject.ybiry.galleryonmap.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.petproject.ybiry.galleryonmap.arch.BaseViewModel;
import com.petproject.ybiry.galleryonmap.arch.BaseViewModelFragment;

public class MainFragmentViewModel extends BaseViewModel implements LifecycleObserver {


    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
    }

}
