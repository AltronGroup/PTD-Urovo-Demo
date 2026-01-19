package com.coldstone.urovocustomerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.coldstone.urovocustomerdemo.sdk.UrovoManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private boolean isScanner = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        UrovoManager urovoManager = UrovoManager.getInstance(this);
        if (!urovoManager.isPrinterAvailable()) {
            findViewById(R.id.btnPrinter).setEnabled(false);
            findViewById(R.id.btnPrinter).setAlpha(0.4f);
        }
        if (!urovoManager.isMagAvailable()) {
            findViewById(R.id.btnMag).setEnabled(false);
            findViewById(R.id.btnMag).setAlpha(0.4f);
        }
        if (!urovoManager.isIccAvailable()) {
            findViewById(R.id.btnIcc).setEnabled(false);
            findViewById(R.id.btnIcc).setAlpha(0.4f);
        }
        if (!urovoManager.isPiccAvailable()) {
            findViewById(R.id.btnPicc).setEnabled(false);
            findViewById(R.id.btnPicc).setAlpha(0.4f);
        }
        if (!urovoManager.isDeviceManagerAvailable()) {
            findViewById(R.id.btnDeviceManager).setEnabled(false);
            findViewById(R.id.btnDeviceManager).setAlpha(0.4f);
        }
    }

    public void onInvokeClick(View view) {
        Intent intent = new Intent(MainActivity.this, InvokeActivity.class);
        startActivity(intent);
    }

    public void onDeviceManagerClick(View view) {
        Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
        startActivity(intent);
    }

    public void onPiccClick(View view) {
    }

    public void onMagClick(View view) {
        Intent intent = new Intent(MainActivity.this, MagActivity.class);
        startActivity(intent);
    }

    public void onIccClick(View view) {
        Intent intent = new Intent(MainActivity.this, IccActivity.class);
        startActivity(intent);
    }

    public void onScannerClick(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    public void onPrinterClick(View view) {
        Intent intent = new Intent(MainActivity.this, PrinterActivity.class);
        startActivity(intent);
    }

    public void onExitClick(View view) {
        finish();
    }

}