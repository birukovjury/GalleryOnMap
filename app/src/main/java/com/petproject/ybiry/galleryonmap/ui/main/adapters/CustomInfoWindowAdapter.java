package com.petproject.ybiry.galleryonmap.ui.main.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.data.model.Photo;
import com.petproject.ybiry.galleryonmap.ui.main.layout.MapWrapperLayout;
import com.petproject.ybiry.galleryonmap.ui.main.listener.OnInfoWindowElemTouchListener;

import org.apache.commons.lang3.StringUtils;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "infoWindowAdapter";
    private static final int MAX_WIDTH = 100;
    private static final int MAX_HEIGHT = 100;
    private final Activity mContext;
    private Cluster<Photo> mCluster;
    private final GoogleMap mMap;
    private OnInfoWindowElemTouchListener mInfoButtonListener;

    public CustomInfoWindowAdapter(Activity context, ClusterManager manager, GoogleMap map) {
        mContext = context;
        mMap = map;
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

        MapWrapperLayout mapWrapperLayout = mContext.findViewById(R.id.map_relative_layout);
        ViewGroup infoWindow = (ViewGroup) mContext.getLayoutInflater().inflate(R.layout.custom_infowindow, null);

        TextView infoTitle = infoWindow.findViewById(R.id.nameTxt);
        TextView infoSnippet = infoWindow.findViewById(R.id.addressTxt);
        Button infoButton1 = infoWindow.findViewById(R.id.btnOne);
        Button infoButton2 = infoWindow.findViewById(R.id.btnTwo);


        mInfoButtonListener = new OnInfoWindowElemTouchListener(infoButton1, mContext.getResources().getDrawable(R.drawable.ic_menu_camera), mContext.getResources().getDrawable(R.drawable.ic_menu_camera)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                // Here we can perform some action triggered after clicking the button
                Toast.makeText(mContext, "click on button 1", Toast.LENGTH_SHORT).show();
            }

        };

        infoButton1.setOnTouchListener(mInfoButtonListener);

        mInfoButtonListener = new OnInfoWindowElemTouchListener(infoButton2, mContext.getResources().getDrawable(R.drawable.ic_menu_camera), mContext.getResources().getDrawable(R.drawable.ic_menu_camera)) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Toast.makeText(mContext.getApplicationContext(), "click on button 2", Toast.LENGTH_LONG).show();
            }
        };
        infoButton2.setOnTouchListener(mInfoButtonListener);
        mapWrapperLayout.init(mMap, getPixelsFromDp(mContext, 39 + 20));

        infoSnippet.setText(marker.getTitle());
        infoTitle.setText(marker.getSnippet());
        mInfoButtonListener.setMarker(marker);

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
        return infoWindow;
    }

    private void setVisibility(boolean visible, ImageView... views) {
        for (ImageView view : views) {
            if (visible)
                view.setVisibility(View.VISIBLE);
            else view.setVisibility(View.GONE);
        }
    }

    private static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

}
