package de.tum.ase.aatqrgenerator.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.activity.MainActivity;

/**
 * Created by Dat on 15.1.2017.
 */

public class QrFragment extends Fragment {
    private ImageView iv_qr;
    private ProgressDialog progress;
    private String verificationToken;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        iv_qr = (ImageView) view.findViewById(R.id.iv_qr);

        return view;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
        new GenerateQr().execute(verificationToken);
    }

    private class GenerateQr extends AsyncTask<String, Void, Bitmap> {

        final static int WIDTH = 200;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "Verification QR",
                    "Generating verification QR, please wait...", true);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                Thread.sleep(1000);
                BitMatrix result;
                try {
                    result = new MultiFormatWriter().encode(params[0],
                            BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
                } catch (IllegalArgumentException iae) {
                    // Unsupported format
                    return null;
                }

                int w = result.getWidth();
                int h = result.getHeight();
                int[] pixels = new int[w * h];
                for (int y = 0; y < h; y++) {
                    int offset = y * w;
                    for (int x = 0; x < w; x++) {
                        pixels[offset + x] = result.get(x, y)
                                ? Color.BLACK : Color.WHITE;
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
                return bitmap;

            } catch (InterruptedException | WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
            if(bitmap == null) {
                Toast.makeText(getActivity(), "Processing error", Toast.LENGTH_SHORT).show();
            } else {
                iv_qr.setImageBitmap(bitmap);
            }
        }
    }
}
