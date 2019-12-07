package com.petproject.ybiry.galleryonmap.ui.main.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.data.model.Photo;

import org.apache.commons.lang3.StringUtils;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "infoWindowAdapter";
    private static final int MAX_WIDTH = 100;
    private static final int MAX_HEIGHT = 100;
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
        titleTextView.setVisibility(View.GONE);

        if (mCluster == null) { //means marker tapped, not cluster
            if (StringUtils.isNoneBlank(marker.getTitle())) {
                titleTextView.setText(marker.getTitle());
                titleTextView.setVisibility(View.VISIBLE);
            }

          /*  Picasso.get()
                    .load(new File(marker.getSnippet()))
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .placeholder(R.drawable.spinner)
                    .into(img1);*/

            titleTextView.setText(marker.getTitle());
            setVisibility(true, img1);
            img1.setImageBitmap(BitmapFactory.decodeFile(marker.getSnippet()));

        } else {
            if (!mCluster.getItems().isEmpty()) { //means cluster tapped, not marker
                Photo[] clusterPhotos = new Photo[mCluster.getItems().size()];
                clusterPhotos = mCluster.getItems().toArray(clusterPhotos);

                switch (clusterPhotos.length) {
                    case 1:
                        img1.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[0].getSnippet()));
                        setVisibility(true, img1);
                        break;
                    case 2:
                        img1.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[0].getSnippet()));
                        img2.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[1].getSnippet()));
                        setVisibility(true, img1, img2);
                        break;
                    case 3:
                        img1.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[0].getSnippet()));
                        img2.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[1].getSnippet()));
                        img3.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[2].getSnippet()));
                        setVisibility(true, img1, img2, img3);
                        break;
                    case 4:
                    default:
                        img1.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[0].getSnippet()));
                        img2.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[1].getSnippet()));
                        img3.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[2].getSnippet()));
                        img4.setImageBitmap(BitmapFactory.decodeFile(clusterPhotos[3].getSnippet()));
                        setVisibility(true, img1, img2, img3, img4);
                }
                titleTextView.setText(mContext.getApplicationContext().getString(R.string.cluster_title));
                titleTextView.setVisibility(View.VISIBLE);
            }
        }
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
