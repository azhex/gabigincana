package com.azhex.gabigincana.herramientas;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.azhex.gabigincana.R;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

public class QREscaner extends Activity {
    private static final String TAG = QREscaner.class.getSimpleName();
    private CompoundBarcodeView barcodeView;
    private BeepManager beepManager;
    private String lastText;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                lastText = result.getText();
                beepManager.playBeepSoundAndVibrate();
                result.getResult().getText();
                Log.i("RESULT SCAN", result.getText());
                Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_LONG);

                return;
            }

            lastText = result.getText();
            beepManager.playBeepSoundAndVibrate();
            result.getResult().getText();
            Log.i("RESULT SCAN", result.getText());
            Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_LONG);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrescaner);

        barcodeView = (CompoundBarcodeView) findViewById(R.id.qrView);
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("Escanea un código qr para obtener puntos");

        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}