package de.tum.ase.aatqrgenerator.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import de.tum.ase.aatqrgenerator.OnSaveQrRequestListener;
import de.tum.ase.aatqrgenerator.R;
import de.tum.ase.aatqrgenerator.activity.MainActivity;

/**
 * Created by Dat on 15.1.2017.
 */

public class QrFragment extends Fragment {

    FloatingActionButton fab_qr;
    ImageView iv_qr;
    OnSaveQrRequestListener listener;

    ProgressDialog progress;

    public QrFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnSaveQrRequestListener) activity;
        } catch(Exception e) {
            Log.e("EEEEE", e.getMessage());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        fab_qr = (FloatingActionButton) view.findViewById(R.id.fab_qr);
        fab_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQr();
            }
        });

        iv_qr = (ImageView) view.findViewById(R.id.iv_qr);

        if(listener != null && listener.getSavedBitmap() != null) {
            iv_qr.setImageBitmap(listener.getSavedBitmap());
        }

        return view;
    }

    public void getQr() {
        //TODO get value from server, convert to QR
        if (progress == null) {
            new FetchSecret().execute("student_id");
        }
    }

    private class FetchSecret extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(QrFragment.this.getActivity(), "QR Code",
                    "Generating QR, please wait...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            //TODO process params
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
            return "Two can keep a secret if one of them is dead.";
        }

        @Override
        protected void onPostExecute(String secret) {
            if (secret == null) {
                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
                Toast.makeText(getActivity(), "Network error", Toast.LENGTH_SHORT).show();
                return;
            }

            new GenerateQr().execute(secret);
        }
    }


    private class GenerateQr extends AsyncTask<String, Void, Bitmap> {

        final static int WIDTH = 200;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
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
                                ? ContextCompat.getColor(getActivity(), R.color.black)
                                : ContextCompat.getColor(getActivity(), R.color.white);
                    }
                }
                Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
                return bitmap;

            } catch (WriterException e) {
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
                listener.onSaveQrRequested(bitmap);
                iv_qr.setImageBitmap(bitmap);
            }
        }
    }
}
