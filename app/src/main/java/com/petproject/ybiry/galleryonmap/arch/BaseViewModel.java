package com.petproject.ybiry.galleryonmap.arch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class BaseViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> mIsLoading = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> isLoading() {
        return mIsLoading;
    }

    protected void setLoading(boolean isLoading) {
        mIsLoading.setValue(isLoading);
    }

    protected void postLoading(boolean isLoading) {
        mIsLoading.postValue(isLoading);
    }

}
