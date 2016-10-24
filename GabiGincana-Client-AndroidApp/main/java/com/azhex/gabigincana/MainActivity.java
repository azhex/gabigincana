package com.azhex.gabigincana;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

public class MainActivity extends AppCompatActivity {
    private BarcodeDetector qr;
    private CameraSource camaraSrc;
    private SurfaceView camaraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camaraView = (SurfaceView) this.findViewById(R.id.camaraView);

        qr = new BarcodeDetector.Builder(getApplicationContext()).setBarcodeFormats(Barcode.QR_CODE).build();
        camaraSrc = new CameraSource.Builder(getApplicationContext(), qr).setRequestedPreviewSize(480, 640).build();


    }
}
