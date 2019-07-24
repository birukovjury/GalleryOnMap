package com.petproject.ybiry.galleryonmap.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.arch.BaseViewModelFragment;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.databinding.FragmentMainBinding;

import java.util.List;
import java.util.Objects;

public class MainFragment extends BaseViewModelFragment<FragmentMainBinding, MainFragmentViewModel>
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "MainFragment";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDependencies();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public MainFragmentViewModel initViewModel() {
        return ViewModelProviders.of(this).get(MainFragmentViewModel.class);
    }

    @Override
    protected void setSubscribers() {
        getLifecycle().addObserver(getViewModel());
        observeForPermissionRequest();
        observeLoadingState();
        observeForToast();
        observeNewPhotos();
    }

    private void initDependencies() {
        getViewModel().init();
    }


    private void observeLoadingState() {
        getViewModel().isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                Log.e(TAG, "Progress state received: " + isLoading);
                setLoadingState(isLoading);
            }
        });
    }

    private void setLoadingState(boolean state) {
        if (state) {
            getBinding().progressBar.setVisibility(View.VISIBLE);
            getBinding().backView.setVisibility(View.VISIBLE);

        } else {
            getBinding().progressBar.setVisibility(View.GONE);
            getBinding().backView.setVisibility(View.GONE);
        }
        mMap.getUiSettings().setAllGesturesEnabled(!state);
    }

    private void observeForToast() {
        getViewModel().getToast().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                Log.e(TAG, "Toast received: " + message);
                showToast(message);
            }
        });
    }

    private void observeNewPhotos() {
        getViewModel().getPhotos().observe(this, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                Log.e(TAG, "New photo received");
                if (photos != null) setMarkers(photos);
                else {
                    showToast("Something went wrong");
                }
            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }


    private void observeForPermissionRequest() {
        getViewModel().getRequestPermissions().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String requiredPermission) {
                Log.e(TAG, "Permission request received: " + requiredPermission);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requiredPermission != null) {
                    ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{requiredPermission},
                            0);
                }
            }
        });
    }


    private void setMarkers(List<Photo> photos) {
        LatLng position;
        int i = photos.size() - 1;
        do {
            position = new LatLng(photos.get(i).getLatitude(), photos.get(i).getLongitude());
            mMap.addMarker(new MarkerOptions().position(position).title("Photo №" + i));
            i--;
        }
        while (0 <= i);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getViewModel().getInitialData();
    }
}
