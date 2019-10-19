package com.petproject.ybiry.galleryonmap.ui.main.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.R;

import org.apache.commons.lang3.StringUtils;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Activity mContext;
    private final ClusterManager mClusterManager;

    public CustomInfoWindowAdapter(Activity context, ClusterManager manager) {
        mContext = context;
        mClusterManager = manager;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = mContext.getLayoutInflater().inflate(R.layout.custom_info_window, null);

        TextView titleTextView = view.findViewById(R.id.tv_title);
        TextView subTitleTextView = view.findViewById(R.id.tv_subtitle);
        ImageView img = view.findViewById(R.id.pic);
        Bitmap myBitmap;

        if (StringUtils.isNoneBlank(marker.getSnippet())) {
            myBitmap = BitmapFactory.decodeFile(marker.getSnippet());
            titleTextView.setText(marker.getTitle());
            img.setImageBitmap(myBitmap);
        } else {
            titleTextView.setText("Cluster");
            img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_menu_camera));
        }

        img.setMaxHeight(50);
        return view;
    }
}
