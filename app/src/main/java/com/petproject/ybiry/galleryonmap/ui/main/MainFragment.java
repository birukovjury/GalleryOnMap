package com.petproject.ybiry.galleryonmap.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.BuildConfig;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.arch.BaseViewModelFragment;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.databinding.FragmentMainBinding;
import com.petproject.ybiry.galleryonmap.ui.main.adapters.CustomInfoWindowAdapter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.petproject.ybiry.galleryonmap.data.model.RequestCode.PERMISSIONS_MULTIPLE_REQUEST;
import static com.petproject.ybiry.galleryonmap.data.model.RequestCode.PERMISSIONS_REQUEST_LOCATION;
import static com.petproject.ybiry.galleryonmap.data.model.RequestCode.PERMISSIONS_REQUEST_STORAGE;

public class MainFragment extends BaseViewModelFragment<FragmentMainBinding, MainFragmentViewModel>
        implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<Photo>,
        ClusterManager.OnClusterInfoWindowClickListener<Photo>,
        ClusterManager.OnClusterItemClickListener<Photo>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Photo> {

    private static final String TAG = "MainFragment";
    private GoogleMap mMap;
    private CustomInfoWindowAdapter mAdapter;
    private ClusterManager<Photo> mClusterManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDependencies();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mMap == null) {
            getAsyncMap();
        }
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

    private void getAsyncMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private GoogleMap getMap() {
        return mMap;
    }


    private void initDependencies() {
        getViewModel().init();
        if (mAdapter == null)
            mAdapter = new CustomInfoWindowAdapter(getActivity(), mClusterManager);
    }


    private void observeLoadingState() {
        getViewModel().isLoading().observe(this, isLoading -> {
            Log.e(TAG, "Progress state received: " + isLoading);
            setLoadingState(isLoading);
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
        if (mMap != null)
            mMap.getUiSettings().setAllGesturesEnabled(!state);
    }


    private void observeForToast() {
        getViewModel().getToast().observe(this, message -> {
            Log.e(TAG, "Toast received: " + message);
            showToast(message);
        });
    }


    private void observeNewPhotos() {
        getViewModel().getPhotos().observe(this, photos -> {
            Log.e(TAG, "New photo received");
            if (photos != null && !photos.isEmpty()) addItems(photos);
            else {
                showToast("Photos with location haven't found");
            }
        });
    }


    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }


    private void observeForPermissionRequest() {
        getViewModel().getRequestPermissions().observe(this, requiredPermissions -> {
            Log.e(TAG, "Permission request received: " + Arrays.toString(requiredPermissions));
            int requestCode = getPermissionRequestCode(requiredPermissions);
            requestPermissions(requiredPermissions, requestCode);
        });
    }

    private int getPermissionRequestCode(String[] requiredPermissions) {
        if (requiredPermissions.length > 1)
            return PERMISSIONS_MULTIPLE_REQUEST;
        else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(requiredPermissions[0]))
            return PERMISSIONS_REQUEST_LOCATION;
        return PERMISSIONS_REQUEST_STORAGE;
    }


    private void addItems(List<Photo> photos) {
        if (mMap != null) {
            for (int i = 0; i < photos.size(); i++) {
                mClusterManager.addItem(photos.get(i));
            }
            moveToCurrentLocation(photos.get(0).getPosition());
        }
    }


    private void moveToCurrentLocation(LatLng currentLocation) {
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 6));
        getMap().animateCamera(CameraUpdateFactory.zoomTo(11), 1000, null);
    }


    private void zoomCluster(Cluster<Photo> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (Photo item : cluster.getItems()) {
            builder.include(new LatLng(item.getPosition().latitude, item.getPosition().longitude));
        }
        final LatLngBounds bounds = builder.build();

        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        } catch (Exception e) {
            Log.e(TAG, "onClusterClick zoom error: " + e.getMessage());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpCluster();
        getViewModel().getInitialData();
    }

    private void setUpCluster() {
        if (mClusterManager == null)
            mClusterManager = new ClusterManager<>(requireContext(), getMap());

        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);

        mClusterManager.cluster();
        getMap().setInfoWindowAdapter(mAdapter);
    }


    @Override
    public boolean onClusterClick(Cluster<Photo> cluster) {
        Log.e(TAG, "onClusterClick");
        mAdapter.setCluster(cluster);
        return false;
    }


    @Override
    public void onClusterInfoWindowClick(Cluster<Photo> cluster) {
        Log.e(TAG, "onClusterInfoWindowClick");
        zoomCluster(cluster);
    }


    @Override
    public boolean onClusterItemClick(Photo photo) {
        Log.e(TAG, "onClusterItemClick");
        mAdapter.setCluster(null);
        return false;
    }


    @Override
    public void onClusterItemInfoWindowClick(Photo photo) {
        Log.e(TAG, "onClusterItemInfoWindowClick");
        openPhotoInGallery(photo);
    }

    private void openPhotoInGallery(Photo photo) {
        File imageFile = new File(photo.getSnippet());
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(requireContext(),
                BuildConfig.APPLICATION_ID + ".provider",
                imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "PERMISSIONS_MULTIPLE_REQUEST");
                    getViewModel().getInitialData();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "PERMISSIONS_REQUEST_LOCATION");
                    getViewModel().getInitialData();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "PERMISSIONS_REQUEST_STORAGE");
                    getViewModel().getInitialData();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}