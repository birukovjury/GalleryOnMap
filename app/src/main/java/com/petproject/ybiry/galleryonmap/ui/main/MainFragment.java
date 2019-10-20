package com.petproject.ybiry.galleryonmap.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
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
import java.util.List;
import java.util.Objects;

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
        getViewModel().getRequestPermissions().observe(this, requiredPermission -> {
            Log.e(TAG, "Permission request received: " + requiredPermission);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requiredPermission != null) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{requiredPermission},
                        0);
            }
        });
    }


    private void addItems(List<Photo> photos) {
        for (int i = 0; i < photos.size(); i++) {
            mClusterManager.addItem(photos.get(i));
        }
        moveToCurrentLocation(photos.get(0).getPosition());
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
            Log.e(TAG, "onClusterClick: " + e.getMessage());
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getViewModel().getInitialData();
        setUpCluster();
    }

    private void setUpCluster() {
        if (mClusterManager == null)
            mClusterManager = new ClusterManager<>(requireContext(), getMap());
        mAdapter = new CustomInfoWindowAdapter(getActivity(), mClusterManager);

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
        showToast("Cluster clicked:");
        Log.e(TAG, "onClusterClick");
        mAdapter.setCluster(cluster);
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Photo> cluster) {
        showToast("Info window clicked:");
        zoomCluster(cluster);
        Log.e(TAG, "onClusterInfoWindowClick");
    }

    @Override
    public boolean onClusterItemClick(Photo photo) {
        showToast("onClusterItemClick");
        Log.e(TAG, "onClusterItemClick");
        mAdapter.setCluster(null);
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Photo photo) {
        showToast("onClusterItemInfoWindowClick");
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
}