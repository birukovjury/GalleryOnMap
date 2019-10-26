package com.petproject.ybiry.galleryonmap.data.model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static com.petproject.ybiry.galleryonmap.data.model.RequestCode.PERMISSIONS_MULTIPLE_REQUEST;
import static com.petproject.ybiry.galleryonmap.data.model.RequestCode.PERMISSIONS_REQUEST_LOCATION;
import static com.petproject.ybiry.galleryonmap.data.model.RequestCode.PERMISSIONS_REQUEST_STORAGE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Local codes of permission request
 *
 * @author Yuriy
 */

@Retention(SOURCE)
@IntDef({
        PERMISSIONS_MULTIPLE_REQUEST,
        PERMISSIONS_REQUEST_LOCATION,
        PERMISSIONS_REQUEST_STORAGE
})
public @interface RequestCode {
    /**
     * Request multi permissions
     */
    int PERMISSIONS_MULTIPLE_REQUEST = 123;
    /**
     * Request just location permission
     */
    int PERMISSIONS_REQUEST_LOCATION = 111;
    /**
     * Request just storage permission
     */
    int PERMISSIONS_REQUEST_STORAGE = 100;
}
