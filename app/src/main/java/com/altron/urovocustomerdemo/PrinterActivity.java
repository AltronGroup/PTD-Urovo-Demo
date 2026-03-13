package com.altron.urovocustomerdemo;

import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.altron.urovocustomerdemo.sdk.UrovoManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Activity that demonstrates thermal printer functionality on Urovo devices.
 * Provides examples of printing text, images, and QR codes using the built-in
 * thermal printer hardware.
 * <p>
 * This activity shows how to:
 * - Print formatted text with different fonts and sizes
 * - Generate and print QR codes
 * - Convert text to bitmap images and print them
 * - Control printer settings like page setup and paper feed
 *
 * @author Urovo Customer Demo Team
 * @version 1.0
 */
public class PrinterActivity extends AppCompatActivity {
    UrovoManager urovoManager;
    PrinterManager printerManager;
    private static final String TAG = "PrinterActivity";

    /**
     * Called when the activity is starting. Initializes the UI with edge-to-edge display
     * and sets up the printer manager for subsequent print operations.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this Bundle contains the most recent data.
     *                          Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_printer);
        urovoManager = UrovoManager.getInstance(this);
        printerManager = urovoManager.getPrinter();
    }


    /**
     * Handles click event for the QR code print button. Generates a QR code
     * containing sample text and prints it on the thermal printer.
     *
     * @param view The view that was clicked
     */
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

    /**
     * Handles click event for the text print button. Demonstrates printing
     * text using different methods (drawText and drawTextEx) with various
     * formatting options including font size, style, and positioning.
     *
     * @param view The view that was clicked
     */
    public void onTextClick(View view) {
        int fontSize = 24;
        int fontStyle = 0x0000;
        String fontName = "simsun";
        int height = 0;
        String sTestString = "0123456789ABCDEF0123456789\n" +
                "9876543210FEDCBA9876543210\n" +
                "The Fox Jumps Over the Rope\n" ;

        String[] texts = sTestString.split("\n");   //Split print content into multiple lines
        printerManager.clearPage();
        for (String text : texts) {
            height += printerManager.drawText(text, 0, height, fontName, fontSize, false, false, 0);   //Printed text
        }
        for (String text : texts) {
            height += printerManager.drawTextEx(text, 5, height, 384, -1, fontName, fontSize, 0, fontStyle, 0);   ////Printed text
        }
        int iResult = printerManager.printPage(0);
        if (iResult != 0) {
            Log.e(TAG, "Print failed with error code: " + iResult);
            Toast.makeText(this, "Print failed. Error code: " + iResult, Toast.LENGTH_SHORT).show();
            return;
        }
        while (printerManager.getStatus() == PrinterManager.PRNSTS_BUSY) {
            // Wait for printing to complete
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Log.e(TAG, "Print text interrupted: " + e.getMessage());
            }
        }
        // Check final status
        int finalStatus = printerManager.getStatus();
        if (finalStatus != 0) {
            Log.e(TAG, "Printer error status: " + finalStatus);
            Toast.makeText(this, "Printer error. Status: " + finalStatus, Toast.LENGTH_SHORT).show();
        }
        printerManager.paperFeed(50);
    }

    /**
     * Handles click event for the image print button. Creates a bitmap image
     * from text and prints it using the thermal printer.
     *
     * @param view The view that was clicked
     */
    public void onImageClick(View view) {
        Bitmap bmp = createTextBitmap("Image generated\nfrom text\n\nExample of printing bitmap");
        if (bmp != null) {
            Log.d(TAG, "Bitmap created successfully");
            printBitmap(bmp);
        } else {
            Log.e(TAG, "Failed to create bitmap");
        }
    }

    /**
     * Handles click event for the exit button. Closes the printer activity
     * and returns to the main menu.
     *
     * @param view The view that was clicked
     */
    public void onExitClick(View view) {
        finish();
    }

    /**
     * Creates a bitmap image from multi-line text with configurable styling.
     * The text is rendered on a white background with black text using
     * the specified padding and line height parameters.
     *
     * @param text The text to render, with lines separated by newline characters
     * @return A Bitmap containing the rendered text, or null if creation fails
     */
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

    /**
     * Prints a bitmap image on the thermal printer. Sets up the page,
     * draws the bitmap, and manages the print queue including paper feed.
     *
     * @param bitmap The bitmap image to print
     */
    public void printBitmap(Bitmap bitmap) {
        try {
            printerManager.clearPage();
            printerManager.setupPage(384, -1);
            printerManager.drawBitmap(bitmap, 0, 0);
            int result = printerManager.printPage(0);
            if (result != 0) {
                Log.e(TAG, "Print failed with error code: " + result);
                Toast.makeText(this, "Print failed. Error code: " + result, Toast.LENGTH_SHORT).show();
                return;
            }
            while (printerManager.getStatus() == PrinterManager.PRNSTS_BUSY) {
                // Wait for printing to complete
                Thread.sleep(100);
            }
            Thread.sleep(200);
            printerManager.paperFeed(200);
        } catch (Exception e) {
            Log.e(TAG, "Print bitmap error: " + e.getMessage(), e);
            Toast.makeText(this, "Print error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Generates a QR code bitmap from the provided text string.
     * Uses ZXing's MultiFormatWriter to encode the text into a 300x300 pixel QR code.
     *
     * @param text The text content to encode in the QR code
     * @return A Bitmap containing the QR code image, or null if generation fails
     */
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

    /**
     * Prints a QR code bitmap on the thermal printer. Sets up the page for
     * 58mm printer width, positions the QR code, and manages the print queue
     * including status checking and paper feed.
     *
     * @param qrBitmap The QR code bitmap to print
     */
    public void printQr(Bitmap qrBitmap) {
        try {
            printerManager.clearPage();
            printerManager.setupPage(384, -1); // 58mm printer width
            printerManager.drawBitmap(qrBitmap, 40, 0); // center-ish
            int result = printerManager.printPage(0);
            if (result != 0) {
                Log.e(TAG, "Print failed with error code: " + result);
                Toast.makeText(this, "Print failed. Error code: " + result, Toast.LENGTH_SHORT).show();
                return;
            }
            while (printerManager.getStatus() == PrinterManager.PRNSTS_BUSY) {
                // Wait for printing to complete
                Thread.sleep(100);
            }
            Thread.sleep(200);
            printerManager.paperFeed(200);
        } catch (Exception e) {
            Log.e(TAG, "Print QR error: " + e.getMessage(), e);
            Toast.makeText(this, "Print error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}