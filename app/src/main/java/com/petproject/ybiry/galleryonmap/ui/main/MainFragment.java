package com.petproject.ybiry.galleryonmap.ui.main;

import android.Manifest;
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
import com.petproject.ybiry.galleryonmap.databinding.FragmentMainBinding;

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
        // if (savedInstanceState == null) {
        //      getViewModel().getInitialData();
        //  }
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
    }

    private void initDependencies() {
        getViewModel().init();
    }


    private void observeLoadingState() {
        getViewModel().isLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                Log.e(TAG, "----------state " + isLoading);
                if (isLoading)
                    getBinding().progressBar.setVisibility(View.VISIBLE);
                else
                    getBinding().progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void observeForToast() {
        getViewModel().getToast().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String message) {
                Log.e(TAG, "----------Message =  " + message);
                showToast(message);
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
                Log.e(TAG, "----------permission =  " + requiredPermission);
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(requiredPermission))
                    requestLocationAccess();
            }
        });
    }

    private void requestLocationAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
