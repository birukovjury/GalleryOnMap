package com.petproject.ybiry.galleryonmap.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;

import com.petproject.ybiry.galleryonmap.arch.BaseViewModel;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.data.repository.Repository;
import com.petproject.ybiry.galleryonmap.data.repository.RepositoryImpl;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainFragmentViewModel extends BaseViewModel implements LifecycleObserver {

    private static final String CLASS_TAG = "MainFrViewModel";

    private MutableLiveData<List<Photo>> mListOfPhotosLiveData;
    private MutableLiveData<String> mToastLiveData;
    private MutableLiveData<String> mRequestPermissionLiveData;

    private Repository mRepo;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

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

        mLocationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = initLocationListener();

        subscribeLocationService();

        if (!isExternalStorageGranted(getApplication().getApplicationContext())) {
            requestExternalStorageReadPermissions();
        }

    }

    void getInitialData() {
        mRepo.getPhotos().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        postLoading(true);
                    }
                })
                .doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        setLoading(false);
                    }
                })
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


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void connect() {
        Log.e(CLASS_TAG, "Lifecycle.Event.ON_START");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void disconnect() {
        Log.e(CLASS_TAG, "Lifecycle.Event.ON_STOP");
        unsubscribeLocationService();
    }

    @SuppressLint("MissingPermission")
    private void subscribeLocationService() {
        Log.e(CLASS_TAG, "Try to subscribe....");
        if (isAboveMarshmallow()) {
            if (isLocationGranted(getApplication().getApplicationContext())) {
                Log.d(CLASS_TAG, "FINE_LOCATION is already given");
                if (mLocationListener != null) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(criteria, true),
                            1000,
                            1f,
                            mLocationListener);

                    Log.e(CLASS_TAG, "Location Listener subscribed");
                }
            } else {
                Log.e(CLASS_TAG, "FINE_LOCATION isn't given yet. Sending request.");
                requestLocationPermissions();
            }
        }
    }

    private void requestLocationPermissions() {
        mRequestPermissionLiveData.postValue(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestExternalStorageReadPermissions() {
        mRequestPermissionLiveData.postValue(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private boolean isAboveMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private boolean isLocationGranted(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isExternalStorageGranted(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private LocationListener initLocationListener() {
        return new LocationListener() {
            CompositeDisposable disposables = new CompositeDisposable();
            final DisposableObserver<String> decodeListener = new DisposableObserver<String>() {
                @Override
                public void onNext(String city) {
                    Log.v(CLASS_TAG, "Received new decoded location from Repo: " + city);
                    //  citySelected(city);
                    unsubscribeLocationService();
                    disposables.clear();
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(CLASS_TAG, "Received: Geocode error" + e.getMessage());
                }

                @Override
                public void onComplete() {
                    Log.v(CLASS_TAG, "Received: Geocode complete");
                }
            };

            @Override
            public void onLocationChanged(Location location) {
                disposables.add(decodeListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    private void unsubscribeLocationService() {
        Log.e(CLASS_TAG, "Try to unsubscribe....");
        if (mLocationListener != null && mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
            Log.e(CLASS_TAG, "mLocationListener unsubscribed");
        }
    }

    LiveData<List<Photo>> getPhotos() {
        return mListOfPhotosLiveData;
    }


    LiveData<String> getToast() {
        return mToastLiveData;
    }

    LiveData<String> getRequestPermissions() {
        return mRequestPermissionLiveData;
    }
}
