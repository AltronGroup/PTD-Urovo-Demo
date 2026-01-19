package com.coldstone.urovocustomerdemo;

import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.coldstone.urovocustomerdemo.sdk.UrovoManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class PrinterActivity extends AppCompatActivity {
    UrovoManager urovoManager;
    PrinterManager printerManager;
    private static final String TAG = "PrinterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_printer);
        urovoManager = UrovoManager.getInstance(this);
        printerManager = urovoManager.getPrinter();
    }


    public void onQRClick(View view) {
        String someText = "Here is a sample QR code text: ";
        Bitmap qrBitmap = generateQrBitmap(someText);

        if (qrBitmap != null) {
            Log.d(TAG, "QR Bitmap generated successfully");
            printQr(qrBitmap);
        }
        else {
            Log.e(TAG, "Failed to generate QR Bitmap");
        }
    }

    public void onTextClick(View view) {
        int fontSize = 24;
        int fontStyle = 0x0000;
        String fontName = "simsun";
        int height = 0;
        String sTestString = "0123456789ABCDEF0123456789\n" +
                "9876543210FEDCBA9876543210\n" +
                "The Fox Jumps Over the Rope\n" ;

        String[] texts = ((String) sTestString).split("\n");   //Split print content into multiple lines
        printerManager.clearPage();
        for (String text : texts) {
            height += printerManager.drawText(text, 0, height, fontName, fontSize, false, false, 0);   //Printed text
        }
        for (String text : texts) {
            height += printerManager.drawTextEx(text, 5, height, 384, -1, fontName, fontSize, 0, fontStyle, 0);   ////Printed text
        }
        int iResult = printerManager.printPage(0);
        while (printerManager.getStatus() == PrinterManager.PRNSTS_BUSY) {
            // Wait for printing to complete
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TAG, "Print text interrupted: " + e.getMessage());
            }
        }
        printerManager.paperFeed(50);
    }

    public void onImageClick(View view) {
        Bitmap bmp = createTextBitmap("Image generated\nfrom text\n\nExample of printing bitmap");
        if (bmp != null) {
            Log.d(TAG, "Bitmap created successfully");
            printBitmap(bmp);
        } else {
            Log.e(TAG, "Failed to create bitmap");
            return;
        }
    }

    public void onExitClick(View view) {
        finish();
    }

    public Bitmap createTextBitmap(String text) {

        int width = 384;
        int padding = 20;
        int lineHeight = 40;

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(32);
        paint.setAntiAlias(true);

        String[] lines = text.split("\n");
        int height = padding * 2 + lineHeight * lines.length;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        int y = padding + lineHeight;

        for (String line : lines) {
            canvas.drawText(line, padding, y, paint);
            y += lineHeight;
        }

        return bitmap;
    }

    public void printBitmap(Bitmap bitmap) {
        try {
            printerManager.clearPage();
            printerManager.setupPage(384, -1);
            printerManager.drawBitmap(bitmap, 0, 0);
            printerManager.printPage(0);
            while (printerManager.getStatus() == PrinterManager.PRNSTS_BUSY) {
                // Wait for printing to complete
                Thread.sleep(100);
            }
            Thread.sleep(200);
            printerManager.paperFeed(200);
        } catch (Exception e) {
            Log.e(TAG, "Print bitmap error: " + e.getMessage());
        }
    }

    public Bitmap generateQrBitmap(String text) {
        int size = 300; // QR size in pixels

        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    size,
                    size
            );

            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(
                            x,
                            y,
                            bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE
                    );
                }
            }

            return bitmap;

        } catch (WriterException e) {
            Log.e(TAG, "QR Code generation error: " + e.getMessage());
            return null;
        }
    }

    public void printQr(Bitmap qrBitmap) {
        try {
            printerManager.clearPage();
            printerManager.setupPage(384, -1); // 58mm printer width
            printerManager.drawBitmap(qrBitmap, 40, 0); // center-ish
            printerManager.printPage(0);
            while (printerManager.getStatus() == PrinterManager.PRNSTS_BUSY) {
                // Wait for printing to complete
                Thread.sleep(100);
            }
            Thread.sleep(200);
            printerManager.paperFeed(200);
        } catch (Exception e) {
            Log.e(TAG, "Print QR error: " + e.getMessage());
        }
    }

}