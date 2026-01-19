package com.coldstone.urovocustomerdemo;

import androidx.camera.core.ImageProxy;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class ZXingHelper {

    public static BinaryBitmap convertImageProxyToBinaryBitmap(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];

        // very simple grayscale conversion from Y plane
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int yValue = planes[0].getBuffer().get(y * width + x) & 0xff;
                int gray = 0xFF000000 | (yValue << 16) | (yValue << 8) | yValue;
                pixels[y * width + x] = gray;
            }
        }

        LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        return new BinaryBitmap(new HybridBinarizer(source));
    }
}
