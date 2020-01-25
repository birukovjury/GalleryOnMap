package com.petproject.ybiry.galleryonmap.ui.main.adapters;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.ui.main.layout.MapWrapperLayout;
import com.petproject.ybiry.galleryonmap.ui.main.listener.OnInfoWindowElemTouchListener;

import org.apache.commons.lang3.StringUtils;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "infoWindowAdapter";
    private final Activity mContext;
    private final ViewGroup mInfoWindow;
    private final OnInfoWindowElemTouchListener mInfoButtonListener;
    private final MapWrapperLayout mMapWrapperLayout;
    private Cluster<Photo> mCluster;

    public CustomInfoWindowAdapter(Activity context,
                                   ViewGroup infoWindow,
                                   OnInfoWindowElemTouchListener infoButtonListener,
                                   MapWrapperLayout mapWrapperLayout) {
        mContext = context;
        mInfoWindow = infoWindow;
        mInfoButtonListener = infoButtonListener;
        mMapWrapperLayout = mapWrapperLayout;
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

        TextView titleTextView = mInfoWindow.findViewById(R.id.tv_title);
        ImageView img1 = mInfoWindow.findViewById(R.id.pic1);
        ImageView img2 = mInfoWindow.findViewById(R.id.pic2);
        ImageView img3 = mInfoWindow.findViewById(R.id.pic3);
        ImageView img4 = mInfoWindow.findViewById(R.id.pic4);

        mInfoButtonListener.setMarker(marker);
        mMapWrapperLayout.setMarkerWithInfoWindow(marker, mInfoWindow);

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
        return mInfoWindow;
    }

    private void setVisibility(boolean visible, ImageView... views) {
        for (ImageView view : views) {
            if (visible)
                view.setVisibility(View.VISIBLE);
            else view.setVisibility(View.GONE);
        }
    }


}
