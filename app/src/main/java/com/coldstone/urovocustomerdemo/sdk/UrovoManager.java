package com.coldstone.urovocustomerdemo.sdk;
import android.content.Context;
import android.device.DeviceManager;
import android.device.IccManager;
import android.device.MagManager;
import android.device.PiccManager;
import android.device.PrinterManager;
import android.device.ScanManager;
import android.nfc.NfcManager;


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
        mDevice = new DeviceManager();
        mScanManager = new ScanManager();
        mIccReader = new IccManager();
        mPiccReader = new PiccManager();
        mMagReader = new MagManager();
        mPrinterManager = new PrinterManager();
    }

    public DeviceManager getDeviceManager() {
        return mDevice;
    }

    public ScanManager getScanner() {
        return mScanManager;
    }

    public PiccManager getPicc() {
        return mPiccReader;
    }

    public IccManager getIcc() {
        return mIccReader;
    }
    public MagManager getMag() {
        return mMagReader;
    }

    public PrinterManager getPrinter() {
        return mPrinterManager;
    }
}
