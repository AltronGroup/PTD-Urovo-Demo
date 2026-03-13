package com.altron.urovocustomerdemo.sdk;
import android.content.Context;
import android.device.DeviceManager;
import android.device.IccManager;
import android.device.MagManager;
import android.device.PiccManager;
import android.device.PrinterManager;
import android.util.Log;

/**
 * Singleton manager class that provides centralized access to all Urovo hardware
 * SDK components. This class initializes and manages instances of various hardware
 * managers including printer, card readers (MAG, ICC, PICC), and device management.
 * <p>
 * This manager follows the Singleton pattern to ensure only one instance exists
 * throughout the application lifecycle. All hardware managers are lazily initialized
 * during construction with proper error handling.
 * <p>
 * Usage example:
 * <pre>
 * UrovoManager manager = UrovoManager.getInstance(context);
 * if (manager.isPrinterAvailable()) {
 *     PrinterManager printer = manager.getPrinter();
 *     // Use printer...
 * }
 * </pre>
 *
 * @author Urovo Customer Demo Team
 * @version 1.0
 */
public class UrovoManager {
    private static UrovoManager instance;

    private DeviceManager mDevice;

    // Example SDK objects (names depend on Urovo SDK)

    private PiccManager mPiccReader;
    private IccManager mIccReader;
    private MagManager mMagReader;

    private PrinterManager mPrinterManager;
    private static final String TAG = "UrovoManager";

    private UrovoManager(Context context) {
        initSdk();
    }

    public static synchronized UrovoManager getInstance(Context context) {
        if (instance == null) {
            instance = new UrovoManager(context);
        }
        return instance;
    }

    private void initSdk() {
        try {
            mDevice = new DeviceManager();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize DeviceManager: " + e.getMessage());
        }

        try {
            mIccReader = new IccManager();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize IccManager: " + e.getMessage());
        }
        try {
            mPiccReader = new PiccManager();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize PiccManager: " + e.getMessage());
        }
        try {
            mMagReader = new MagManager();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize MagManager: " + e.getMessage());
        }
        try {
            mPrinterManager = new PrinterManager();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize PrinterManager: " + e.getMessage());
        }
    }

    /**
     * Checks if the DeviceManager is available and initialized successfully.
     *
     * @return true if DeviceManager is available, false otherwise
     */
    public boolean isDeviceManagerAvailable() {
        return mDevice != null;
    }

    /**
     * Gets the DeviceManager instance for accessing device information and
     * system management functions.
     *
     * @return DeviceManager instance, or null if not available
     */
    public DeviceManager getDeviceManager() {
        return mDevice;
    }

    /**
     * Checks if the PICC (contactless card) reader is available and initialized successfully.
     *
     * @return true if PICC reader is available, false otherwise
     */
    public boolean isPiccAvailable() {
        return mPiccReader != null;
    }

    /**
     * Gets the PiccManager instance for contactless card operations (NFC, Mifare, etc.).
     *
     * @return PiccManager instance, or null if not available
     */
    public PiccManager getPicc() {
        return mPiccReader;
    }

    /**
     * Checks if the ICC (smart card) reader is available and initialized successfully.
     *
     * @return true if ICC reader is available, false otherwise
     */
    public boolean isIccAvailable() {
        return mIccReader != null;
    }

    /**
     * Gets the IccManager instance for smart card (chip card) operations.
     *
     * @return IccManager instance, or null if not available
     */
    public IccManager getIcc() {
        return mIccReader;
    }

    /**
     * Checks if the MAG (magnetic stripe) reader is available and initialized successfully.
     *
     * @return true if MAG reader is available, false otherwise
     */
    public boolean isMagAvailable() {
        return mMagReader != null;
    }

    /**
     * Gets the MagManager instance for magnetic stripe card reading operations.
     *
     * @return MagManager instance, or null if not available
     */
    public MagManager getMag() {
        return mMagReader;
    }

    /**
     * Checks if the thermal printer is available and initialized successfully.
     *
     * @return true if printer is available, false otherwise
     */
    public boolean isPrinterAvailable() {
        return mPrinterManager != null;
    }

    /**
     * Gets the PrinterManager instance for thermal printing operations.
     *
     * @return PrinterManager instance, or null if not available
     */
    public PrinterManager getPrinter() {
        return mPrinterManager;
    }
}
