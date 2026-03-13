package com.altron.urovocustomerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.altron.urovocustomerdemo.sdk.UrovoManager;

/**
 * Main activity that serves as the entry point for the Urovo Customer Demo application.
 * Provides navigation to various hardware feature demonstrations including scanner, printer,
 * card readers (MAG, ICC, PICC/NFC), device information, and Cendroid invocation examples.
 * <p>
 * This activity automatically detects available hardware capabilities and enables/disables
 * corresponding UI buttons based on device capabilities.
 *
 * @author Urovo Customer Demo Team
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";


    /**
     * Called when the activity is starting. Sets up the UI with edge-to-edge display,
     * initializes the Urovo hardware manager, and configures button availability based
     * on detected hardware capabilities.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this Bundle contains the most recent data.
     *                          Otherwise it is null.
     */
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

    /**
     * Handles click event for the Invoke button. Launches the InvokeActivity
     * which demonstrates how to invoke Cendroid system applications.
     *
     * @param view The view that was clicked
     */
    public void onInvokeClick(View view) {
        Intent intent = new Intent(MainActivity.this, InvokeActivity.class);
        startActivity(intent);
    }

    /**
     * Handles click event for the Device Manager button. Launches the DeviceInfoActivity
     * which displays device information and system management functions.
     *
     * @param view The view that was clicked
     */
    public void onDeviceManagerClick(View view) {
        Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
        startActivity(intent);
    }

    /**
     * Handles click event for the PICC/NFC button. Launches the NfcActivity
     * which demonstrates contactless card reading functionality.
     *
     * @param view The view that was clicked
     */
    public void onPiccClick(View view) {
        Intent intent = new Intent(MainActivity.this, NfcActivity.class);
        startActivity(intent);
    }

    /**
     * Handles click event for the MAG button. Launches the MagActivity
     * which demonstrates magnetic stripe card reading functionality.
     *
     * @param view The view that was clicked
     */
    public void onMagClick(View view) {
        Intent intent = new Intent(MainActivity.this, MagActivity.class);
        startActivity(intent);
    }

    /**
     * Handles click event for the ICC button. Launches the IccActivity
     * which demonstrates smart card (IC card) reading functionality.
     *
     * @param view The view that was clicked
     */
    public void onIccClick(View view) {
        Intent intent = new Intent(MainActivity.this, IccActivity.class);
        startActivity(intent);
    }

    /**
     * Handles click event for the Scanner button. Launches the CameraActivity
     * which demonstrates barcode and QR code scanning using the device camera.
     *
     * @param view The view that was clicked
     */
    public void onScannerClick(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        startActivity(intent);
    }

    /**
     * Handles click event for the Printer button. Launches the PrinterActivity
     * which demonstrates thermal printer functionality.
     *
     * @param view The view that was clicked
     */
    public void onPrinterClick(View view) {
        Intent intent = new Intent(MainActivity.this, PrinterActivity.class);
        startActivity(intent);
    }

    /**
     * Handles click event for the Exit button. Closes the main activity
     * and terminates the application.
     *
     * @param view The view that was clicked
     */
    public void onExitClick(View view) {
        finish();
    }

}