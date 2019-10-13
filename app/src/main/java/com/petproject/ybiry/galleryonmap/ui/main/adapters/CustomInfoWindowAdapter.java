package com.petproject.ybiry.galleryonmap.ui.main.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.petproject.ybiry.galleryonmap.R;
import com.petproject.ybiry.galleryonmap.data.model.Photo;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Activity mContext;

    public CustomInfoWindowAdapter(Activity context) {
        mContext = context;
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


        Bitmap myBitmap = BitmapFactory.decodeFile(marker.getSnippet());
        titleTextView.setText(marker.getTitle());
        img.setMaxHeight(50);
        img.setImageBitmap(myBitmap);

        return view;
    }
}
