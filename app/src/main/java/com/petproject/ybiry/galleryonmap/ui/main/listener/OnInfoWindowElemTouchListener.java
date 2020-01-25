package com.petproject.ybiry.galleryonmap.ui.main.listener;


import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.Marker;

public abstract class OnInfoWindowElemTouchListener implements View.OnTouchListener {
    private final View mView;
    private final Drawable mBgDrawableNormal;
    private final Drawable mBgDrawablePressed;
    private final Handler mHandler = new Handler();

    private Marker mMarker;
    private boolean mPressed = false;
    private final Runnable mConfirmClickRunnable = new Runnable() {
        public void run() {
            if (endPress()) {
                onClickConfirmed(mView, mMarker);
            }
        }
    };

    protected OnInfoWindowElemTouchListener(View view, Drawable bgDrawableNormal, Drawable bgDrawablePressed) {
        mView = view;
        mBgDrawableNormal = bgDrawableNormal;
        mBgDrawablePressed = bgDrawablePressed;
    }

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
        if (0 <= event.getX() && event.getX() <= mView.getWidth() &&
                0 <= event.getY() && event.getY() <= mView.getHeight()) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startPress();
                    break;

                // We need to delay releasing of the mView a little so it shows the mPressed state on the screen
                case MotionEvent.ACTION_UP:
                    mHandler.postDelayed(mConfirmClickRunnable, 150);
                    break;

                case MotionEvent.ACTION_CANCEL:
                    endPress();
                    break;
                default:
                    break;
            }
        } else {
            // If the touch goes outside of the mView's area
            // (like when moving finger out of the mPressed button)
            // just release the press
            endPress();
        }
        return false;
    }

    private void startPress() {
        if (!mPressed) {
            mPressed = true;
            mHandler.removeCallbacks(mConfirmClickRunnable);
            mView.setBackground(mBgDrawablePressed);
            if (mMarker != null)
                mMarker.showInfoWindow();
        }
    }

    private boolean endPress() {
        if (mPressed) {
            mPressed = false;
            mHandler.removeCallbacks(mConfirmClickRunnable);
            mView.setBackground(mBgDrawableNormal);
            if (mMarker != null)
                mMarker.showInfoWindow();
            return true;
        } else
            return false;
    }

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}
