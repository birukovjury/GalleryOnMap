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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.data.model.Photo;

import java.util.Collection;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "infoWindowAdapter";
    private final Activity mContext;
    private Cluster<Photo> mCluster;

    public CustomInfoWindowAdapter(Activity context, ClusterManager manager) {
        mContext = context;
    }

    public void setCluster(Cluster<Photo> mCluster) {
        this.mCluster = mCluster;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = mContext.getLayoutInflater().inflate(R.layout.custom_info_window, null);

        TextView titleTextView = view.findViewById(R.id.tv_title);
        ImageView img1 = view.findViewById(R.id.pic1);
        ImageView img2 = view.findViewById(R.id.pic2);
        ImageView img3 = view.findViewById(R.id.pic3);
        ImageView img4 = view.findViewById(R.id.pic4);
        setVisibility(false, img1, img2, img3, img4);

        if (mCluster == null) {
            Bitmap bitmap = BitmapFactory.decodeFile(marker.getSnippet());
            titleTextView.setText(marker.getTitle());
            img1.setImageBitmap(bitmap);
            setVisibility(true, img1);
        } else {
            Collection<Photo> markers = mCluster.getItems();
            Log.d(TAG, "ClusterSize = " + markers.size());
            titleTextView.setText(mContext.getApplicationContext().getString(R.string.cluster_title));
            if (!markers.isEmpty()) {
                Photo[] array = new Photo[markers.size()];
                array = markers.toArray(array);
                switch (markers.size()) {
                    case 1:
                        img1.setImageBitmap(BitmapFactory.decodeFile(array[0].getSnippet()));
                        setVisibility(true, img1);
                        break;
                    case 2:
                        img1.setImageBitmap(BitmapFactory.decodeFile(array[0].getSnippet()));
                        img2.setImageBitmap(BitmapFactory.decodeFile(array[1].getSnippet()));
                        setVisibility(true, img1, img2);
                        break;
                    case 3:
                        img1.setImageBitmap(BitmapFactory.decodeFile(array[0].getSnippet()));
                        img2.setImageBitmap(BitmapFactory.decodeFile(array[1].getSnippet()));
                        img3.setImageBitmap(BitmapFactory.decodeFile(array[2].getSnippet()));
                        setVisibility(true, img1, img2, img3);
                        break;
                    case 4:
                    default:
                        img1.setImageBitmap(BitmapFactory.decodeFile(array[0].getSnippet()));
                        img2.setImageBitmap(BitmapFactory.decodeFile(array[1].getSnippet()));
                        img3.setImageBitmap(BitmapFactory.decodeFile(array[2].getSnippet()));
                        img4.setImageBitmap(BitmapFactory.decodeFile(array[3].getSnippet()));
                        setVisibility(true, img1, img2, img3, img4);
                }
            }
        }

        setMaxHeight(30, img1, img2, img3, img4);
        return view;
    }

    private void setVisibility(boolean visible, ImageView... views) {
        for (ImageView view : views) {
            if (visible)
                view.setVisibility(View.VISIBLE);
            else view.setVisibility(View.GONE);
        }
    }

    private void setMaxHeight(int height, ImageView... views) {
        for (ImageView view : views) {
            view.setMaxHeight(height);
        }
    }
}
