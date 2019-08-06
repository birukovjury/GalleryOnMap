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
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.arch.BaseViewModelFragment;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.databinding.FragmentMainBinding;
import com.petproject.ybiry.galleryonmap.ui.main.adapters.CustomInfoWindowAdapter;

import java.util.List;
import java.util.Objects;

public class MainFragment extends BaseViewModelFragment<FragmentMainBinding, MainFragmentViewModel>
        implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private CustomInfoWindowAdapter mAdapter;
    private ClusterManager<Photo> mClusterManager;
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


    private GoogleMap getMap() {
        return mMap;
    }


    private void initDependencies() {
        getViewModel().init();
        mAdapter = new CustomInfoWindowAdapter(getActivity());
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
                if (photos != null && !photos.isEmpty()) setMarkers(photos);
                else {
                    showToast("Photo with location haven't found");
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
        for (int i = 0; i < photos.size(); i++) {
            mClusterManager.addItem(photos.get(i));
        }
        moveToCurrentLocation(photos.get(0).getPosition());
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 6));
        getMap().animateCamera(CameraUpdateFactory.zoomTo(11), 1000, null);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager<Photo>(requireContext(), getMap());
        getViewModel().getInitialData();
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setInfoWindowAdapter(mAdapter);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        showToast("Marker clicked:" + marker.getId());
    }
}