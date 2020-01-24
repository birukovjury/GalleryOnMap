package com.petproject.ybiry.galleryonmap.ui.main;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.arch.BaseViewModelActivity;
import com.petproject.ybiry.galleryonmap.databinding.ActivityMainBinding;

public class MainActivity extends BaseViewModelActivity<ActivityMainBinding, MainActivityViewModel> {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment(), "main_fragment")
                    .commit();
        }
    }

    @Override
    public MainActivityViewModel initViewModel() {
        return null;
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
