package com.petproject.ybiry.galleryonmap.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.BuildConfig;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.arch.BaseViewModelFragment;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.databinding.FragmentMainBinding;
import com.petproject.ybiry.galleryonmap.ui.main.adapters.CustomInfoWindowAdapter;
import com.petproject.ybiry.galleryonmap.ui.main.layout.MapWrapperLayout;
import com.petproject.ybiry.galleryonmap.ui.main.listener.OnInfoWindowElemTouchListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainFragment extends BaseViewModelFragment<FragmentMainBinding, MainFragmentViewModel>
        implements OnMapReadyCallback,
        ClusterManager.OnClusterClickListener<Photo>,
        ClusterManager.OnClusterInfoWindowClickListener<Photo>,
        ClusterManager.OnClusterItemClickListener<Photo>,
        ClusterManager.OnClusterItemInfoWindowClickListener<Photo> {

    private static final String TAG = "MainFragment";
    private static final int PERMISSIONS_REQUEST_STORAGE = 100;

    private MapWrapperLayout mMapWrapperLayout;
    private ViewGroup mInfoWindow;
    private Button mInfoButton;
    private OnInfoWindowElemTouchListener mInfoButtonListener;

    private GoogleMap mMap;
    private CustomInfoWindowAdapter mAdapter;
    private ClusterManager<Photo> mClusterManager;

    private static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mMap == null) {
            getAsyncMap();
        }
        initViews();
        initListener(mInfoButton);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener(Button button) {
        if (mInfoButtonListener == null)
            mInfoButtonListener = new OnInfoWindowElemTouchListener(button,
                    getResources().getDrawable(R.drawable.common_google_signin_btn_icon_dark_normal_background),
                    getResources().getDrawable(R.drawable.common_google_signin_btn_icon_light_normal_background)) {
                @Override
                protected void onClickConfirmed(View v, Marker marker) {
                    showToast(marker.getSnippet());
                }
            };
        button.setOnTouchListener(mInfoButtonListener);
    }

    @SuppressLint("InflateParams")
    private void initViews() {
        mMapWrapperLayout = getBinding().mapRelativeLayout;
        mInfoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.custom_info_window, null);
        mInfoButton = mInfoWindow.findViewById(R.id.anyButton);
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
        getViewModel().getRequestPermissions().observe(getViewLifecycleOwner(), this::requestPermissions);
        getViewModel().getPhotos().observe(getViewLifecycleOwner(), this::receiveNewPhotos);
        getViewModel().isLoading().observe(getViewLifecycleOwner(), this::setLoadingState);
        getViewModel().getToast().observe(getViewLifecycleOwner(), this::showToast);
    }

    private void getAsyncMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private GoogleMap getMap() {
        return mMap;
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


    private void receiveNewPhotos(List<Photo> photos) {
        Log.e(TAG, "New photo received");
        if (photos != null && !photos.isEmpty()) addItems(photos);
        else {
            showToast("Photos with location haven't found");
        }
    }

    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void requestPermissions(String[] requiredPermissions) {
        Log.e(TAG, "Permission request received: " + Arrays.toString(requiredPermissions));
        requestPermissions(requiredPermissions, PERMISSIONS_REQUEST_STORAGE);
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
        mMapWrapperLayout.init(mMap, getPixelsFromDp(requireContext(), 39 + 20));
        if (mAdapter == null)
            mAdapter = new CustomInfoWindowAdapter(getActivity(),
                    mInfoWindow,
                    mInfoButtonListener,
                    mMapWrapperLayout);
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
                                           @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "PERMISSIONS_REQUEST_STORAGE");
                getViewModel().getInitialData();
            }
        }
    }
}