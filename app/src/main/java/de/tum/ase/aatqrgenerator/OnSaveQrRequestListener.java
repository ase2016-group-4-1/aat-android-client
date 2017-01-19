package de.tum.ase.aatqrgenerator;

import android.graphics.Bitmap;

/**
 * Created by Dat on 16.1.2017.
 */

public interface OnSaveQrRequestListener {
    void onSaveQrRequested(Bitmap bitmap);
    Bitmap getSavedBitmap();
}
