package com.coldstone.urovocustomerdemo.sdk;
import android.content.Context;
import android.device.DeviceManager;
import android.device.IccManager;
import android.device.MagManager;
import android.device.PiccManager;
import android.device.PrinterManager;
import android.device.ScanManager;
import android.nfc.NfcManager;
import android.util.Log;


public class UrovoManager {
    private static UrovoManager instance;
    private Context appContext;

    private DeviceManager mDevice;

    // Example SDK objects (names depend on Urovo SDK)
    private ScanManager mScanManager;
    private PiccManager mPiccReader;
    private IccManager mIccReader;
    private MagManager mMagReader;

    private PrinterManager mPrinterManager;
    private static final String TAG = "UrovoManager";

    private UrovoManager(Context context) {
        appContext = context.getApplicationContext();
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
            mScanManager = new ScanManager();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize ScanManager: " + e.getMessage());
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

    public boolean isDeviceManagerAvailable() {
        return mDevice != null;
    }

    public DeviceManager getDeviceManager() {
        return mDevice;
    }

    public boolean isScannerAvailable() {
        return mScanManager != null;
    }
    public ScanManager getScanner() {
        return mScanManager;
    }

    public boolean isPiccAvailable() {
        return mPiccReader != null;
    }
    public PiccManager getPicc() {
        return mPiccReader;
    }

    public boolean isIccAvailable() {
        return mIccReader != null;
    }
    public IccManager getIcc() {
        return mIccReader;
    }

    public boolean isMagAvailable() {
        return mMagReader != null;
    }
    public MagManager getMag() {
        return mMagReader;
    }

    public boolean isPrinterAvailable() {
        return mPrinterManager != null;
    }
    public PrinterManager getPrinter() {
        return mPrinterManager;
    }
}
