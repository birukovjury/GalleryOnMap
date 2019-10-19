package com.petproject.ybiry.galleryonmap.ui.main.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.data.model.Photo;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "infoWindowAdapter";
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
        ImageView img1 = view.findViewById(R.id.pic1);
        ImageView img2 = view.findViewById(R.id.pic2);
        ImageView img3 = view.findViewById(R.id.pic3);
        ImageView img4 = view.findViewById(R.id.pic4);


        Bitmap myBitmap;

        if (StringUtils.isNoneBlank(marker.getSnippet())) {
            myBitmap = BitmapFactory.decodeFile(marker.getSnippet());
            titleTextView.setText(marker.getTitle());
            img1.setImageBitmap(myBitmap);
            setVisibility(true, img1);
        } else {
            List<Bitmap> bitmaps = new LinkedList<>();
            int markersCount = mClusterManager.getAlgorithm().getItems().size();

            Log.d(TAG, "ClusterSize count = " + markersCount);

            Collection<Photo> markers = mClusterManager.getAlgorithm().getItems();
            for (Photo m : markers) {
                bitmaps.add(BitmapFactory.decodeFile(m.getSnippet()));
                Log.d(TAG, "snipped = " + m.getSnippet());
            }


            Log.d(TAG, "bitmaps count = " + bitmaps.size());

            if (!bitmaps.isEmpty()) {
                titleTextView.setText("Cluster title");
                img1.setImageBitmap(bitmaps.get(0));
                img2.setImageBitmap(bitmaps.get(1));
                img3.setImageBitmap(bitmaps.get(2));
                img4.setImageBitmap(bitmaps.get(3));
                setVisibility(true, img1, img2, img3, img4);
            }

            // img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_menu_camera));
        }

        img1.setMaxHeight(50);
        return view;
    }

    private void setVisibility(boolean visible, ImageView... views) {
        for (ImageView view : views) {
            if (visible)
                view.setVisibility(View.VISIBLE);
            else view.setVisibility(View.GONE);
        }
    }
}
