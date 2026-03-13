package com.altron.urovocustomerdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.RGBLuminanceSource;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity that demonstrates barcode and QR code scanning using the device camera.
 * Uses CameraX for camera preview and ZXing library for barcode decoding.
 * <p>
 * This activity requests camera permissions, displays a live camera preview,
 * and continuously analyzes frames to detect and decode barcodes/QR codes in real-time.
 *
 * @author Urovo Customer Demo Team
 * @version 1.0
 */
public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private TextView txtBarcode;
    private ExecutorService cameraExecutor;
    private static final int CAMERA_REQUEST_CODE = 1001;

    /**
     * Called when the activity is starting. Initializes the UI components,
     * creates a camera executor thread, and requests camera permissions if needed.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this Bundle contains the most recent data.
     *                          Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        txtBarcode = findViewById(R.id.txtBarcode);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }
    }

    /**
     * Initializes and starts the camera for barcode scanning.
     * Sets up camera preview, configures the back camera, and establishes
     * an image analyzer that continuously scans for barcodes in the camera feed.
     * Uses ZXing's MultiFormatReader to decode various barcode formats.
     */
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview
                androidx.camera.core.Preview preview = new androidx.camera.core.Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Camera selector
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Analyzer
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                MultiFormatReader reader = new MultiFormatReader();

                imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy image) {
                        try {
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);

                            int width = image.getWidth();
                            int height = image.getHeight();
                            int[] pixels = new int[width * height];

                            // convert Y plane to gray scale
                            for (int i = 0; i < bytes.length && i < pixels.length; i++) {
                                int v = bytes[i] & 0xff;
                                pixels[i] = 0xFF000000 | (v << 16) | (v << 8) | v;
                            }

                            LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                            final String result = reader.decodeWithState(bitmap).getText();

                            runOnUiThread(() -> txtBarcode.setText(result));

                        } catch (NotFoundException ignored) {
                            // No barcode detected in this frame
                        } catch (Exception e) {
                            Log.e("CameraActivity", "Error analyzing barcode image", e);
                        } finally {
                            image.close();
                        }
                    }
                });

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e("CameraActivity", "Camera initialization failed", e);
                Toast.makeText(this, "Camera init failed", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Called when the activity is being destroyed. Shuts down the camera executor
     * service to release resources and prevent memory leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    /**
     * Callback for the result from requesting permissions. Handles camera permission
     * grant/denial. If permission is granted, starts the camera. If denied, shows
     * a toast message and closes the activity.
     *
     * @param requestCode  The request code passed in requestPermissions()
     * @param permissions  The requested permissions
     * @param grantResults The grant results for the corresponding permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

