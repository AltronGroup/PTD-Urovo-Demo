package com.coldstone.urovocustomerdemo;

import android.device.MagManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.coldstone.urovocustomerdemo.sdk.UrovoManager;


public class MagActivity extends AppCompatActivity {
    public final static int MESSAGE_OPEN_MAG = 1;
    public final static int MESSAGE_CHECK_FAILE = 2;
    public final static int MESSAGE_READ_MAG = 3;
    public final static int MESSAGE_CHECK_OK = 4;
    private MagReaderThread magReaderThread;
    private UrovoManager urovoManager;
    private MagManager magManager;
    private EditText edtCardData;
    private TextView txtStatus;
    private static final String TAG = "MagActivity";

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ_MAG:
                    updateAlert("Read the card successed!");
                    String track1 = msg.getData().getString("track");

                    edtCardData.append(track1);
                    break;
                case MESSAGE_OPEN_MAG:
                    updateAlert("Init Mag Reader failed!");
                    break;
                case MESSAGE_CHECK_FAILE:
                    updateAlert("Please Swipe Card");
                    break;
                case MESSAGE_CHECK_OK:
                    updateAlert("Please Swipe Card!");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mag);
        edtCardData = findViewById(R.id.edtCardData);
        txtStatus = findViewById(R.id.txtStatus);
        urovoManager = UrovoManager.getInstance(this);
        magManager = urovoManager.getMag();
    }

    public void onBackClick(View view) {
        if (magReaderThread != null) {
            magReaderThread.stopMagReader();
            magReaderThread = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (magReaderThread != null) {
            magReaderThread.stopMagReader();
            magReaderThread = null;
        }
        //Initialize and start the swipe thread
        magReaderThread = new MagReaderThread();
        magReaderThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (magManager != null) {
            /*
                4:close() Turn off MSR card reading
             */
            magManager.close();
        }
        if (magReaderThread != null) {
            magReaderThread.stopMagReader();
            magReaderThread = null;
        }
    }

    //Page update status display
    private void updateAlert(String mesg) {
        txtStatus.setText(mesg);
    }

    private class MagReaderThread extends Thread {

        //Flag
        private boolean running = false;

        @Override
        public synchronized void start() {
            super.start();
            running = true;
        }

        public void stopMagReader() {
            running = false;
        }

        @Override
        public void run() {
            super.run();
            /*
                1: Open MSR card reading and return 0 to open successfully
            */
            if (magManager != null) {
                int ret = magManager.open();
                if (ret != 0) {
                    mHandler.sendEmptyMessage(MESSAGE_OPEN_MAG);
                    return;
                }
            }
            while (running) {
                if (magManager == null)
                    return;
                /*
                    2:  Check whether the card swiping operation occurs. This action needs to be performed in a circular manner
                        If 0 is returned, swipe card is detected
                */
                int ret = magManager.checkCard();
                if (ret != 0) {
                    mHandler.sendEmptyMessage(MESSAGE_CHECK_FAILE);
                    try {
                        Thread.sleep(600);
                    } catch (Exception e) {
                        Log.e(TAG, "MagReaderThread run: ", e);
                    }
                    continue;
                } else {
                    mHandler.sendEmptyMessage(MESSAGE_CHECK_OK);
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        Log.e(TAG, "MagReaderThread run: ", e);
                    }
                }
                StringBuffer trackOne = new StringBuffer();
                //Storing track information
                byte[] stripInfo = new byte[1024];

                /*
                    3: Read track information
                        stripInfo:Information is stored in t-l-v (tag length value) format
                              Mark as 1 byte, meaning as follows:
                                            01:track1；
                                            02:track2；
                                            03:track3；
                              The length is 1 byte.
                */
                int allLen = magManager.getAllStripInfo(stripInfo);

                //Analyzing track information
                if (allLen > 0) {
                    //track1
                    int len = stripInfo[1];
                    if (len != 0)
                        trackOne.append(" track1: " + new String(stripInfo, 2, len));
                    //track2
                    int len2 = stripInfo[3 + len];
                    if (len2 != 0)
                        trackOne.append(" \ntrack2: " + new String(stripInfo, 4 + len, len2));
                    //track3
                    int len3 = stripInfo[5 + len + len2];
                    if (len3 != 0 && len3 < 1024)
                        trackOne.append(" \ntrack3: " + new String(stripInfo, 6 + len + len2, len3));

                    //Notify main thread to read information
                    if (!trackOne.toString().equals("")) {
                        trackOne.append("\n");
                        mHandler.removeMessages(MESSAGE_CHECK_FAILE);
                        Message msg = mHandler.obtainMessage(MESSAGE_READ_MAG);
                        Bundle bundle = new Bundle();
                        bundle.putString("track", trackOne.toString());
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                    trackOne = null;
                }
                try {
                    Thread.sleep(800);
                } catch (Exception e) {
                    Log.e(TAG, "MagReaderThread run: ", e);
                }
            }
        }
    }
}