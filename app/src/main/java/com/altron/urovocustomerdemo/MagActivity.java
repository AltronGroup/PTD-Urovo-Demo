package com.altron.urovocustomerdemo;

import android.device.MagManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.altron.urovocustomerdemo.sdk.UrovoManager;

import java.lang.ref.WeakReference;

/**
 * Activity that demonstrates magnetic stripe card reader functionality.
 * Provides real-time card swiping detection and displays track data from
 * magnetic cards (credit cards, ID cards, etc.).
 * <p>
 * This activity uses a background thread to continuously monitor for card
 * swipe events and parses the track information in T-L-V (Tag-Length-Value) format.
 * Supports reading up to three tracks of magnetic stripe data.
 *
 * @author Urovo Customer Demo Team
 * @version 1.0
 */
public class MagActivity extends AppCompatActivity {
    /** Message code for MAG reader initialization failure */
    public final static int MESSAGE_OPEN_MAG = 1;
    /** Message code for card check failure (no card detected) */
    public final static int MESSAGE_CHECK_FAILE = 2;
    /** Message code for successful card read */
    public final static int MESSAGE_READ_MAG = 3;
    /** Message code for successful card detection */
    public final static int MESSAGE_CHECK_OK = 4;
    
    private MagReaderThread magReaderThread;
    private UrovoManager urovoManager;
    private MagManager magManager;
    private EditText edtCardData;
    private TextView txtStatus;
    private static final String TAG = "MagActivity";

    /**
     * Handler for processing messages from the MAG reader background thread.
     * Updates UI based on card reading events and status changes.
     */
    private final MagHandler mHandler = new MagHandler(this);

    /**
     * Static Handler class to prevent memory leaks.
     * Uses WeakReference to avoid holding strong reference to Activity.
     */
    private static class MagHandler extends Handler {
        private final WeakReference<MagActivity> activityRef;

        MagHandler(MagActivity activity) {
            super(Looper.getMainLooper());
            this.activityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MagActivity activity = activityRef.get();
            if (activity == null) return;

            switch (msg.what) {
                case MESSAGE_READ_MAG:
                    activity.updateAlert("Read the card successed!");
                    String track1 = msg.getData().getString("track");
                    if (track1 != null) {
                        activity.edtCardData.append(track1);
                    }
                    break;
                case MESSAGE_OPEN_MAG:
                    activity.updateAlert("Init Mag Reader failed!");
                    break;
                case MESSAGE_CHECK_FAILE:
                    activity.updateAlert("Please Swipe Card");
                    break;
                case MESSAGE_CHECK_OK:
                    activity.updateAlert("Please Swipe Card!");
                    break;
            }
        }
    }

    /**
     * Called when the activity is starting. Initializes the UI with edge-to-edge display
     * and sets up the magnetic card reader manager.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                          being shut down, this Bundle contains the most recent data.
     *                          Otherwise it is null.
     */
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

    /**
     * Handles click event for the back button. Stops the MAG reader thread
     * and closes the activity.
     *
     * @param view The view that was clicked
     */
    public void onBackClick(View view) {
        if (magReaderThread != null) {
            magReaderThread.stopMagReader();
            magReaderThread = null;
        }
        // Use the back button behavior which properly navigates to parent activity
        getOnBackPressedDispatcher().onBackPressed();
    }

    /**
     * Called when the activity is about to become visible. Initializes and starts
     * the MAG reader background thread to begin monitoring for card swipes.
     */
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

    /**
     * Called when the activity is no longer visible. Closes the MAG reader
     * and stops the background monitoring thread to release resources.
     */
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

    /**
     * Called when the activity is being destroyed. Ensures all resources are properly
     * released, including stopping the MAG reader thread and removing handler callbacks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (magReaderThread != null) {
            magReaderThread.stopMagReader();
            magReaderThread.interrupt();
            magReaderThread = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * Updates the status text view with the provided message.
     *
     * @param mesg The message to display in the status text view
     */
    private void updateAlert(String mesg) {
        txtStatus.setText(mesg);
    }

    /**
     * Background thread that continuously monitors the magnetic card reader
     * for card swipe events. Opens the MAG reader, checks for cards in a loop,
     * and parses track data when a card is detected.
     */
    private class MagReaderThread extends Thread {

        /** Flag to control thread execution */
        private boolean running = false;

        /**
         * Starts the thread and sets the running flag to true.
         */
        @Override
        public synchronized void start() {
            super.start();
            running = true;
        }

        /**
         * Stops the MAG reader thread by setting the running flag to false.
         */
        public void stopMagReader() {
            running = false;
        }

        /**
         * Main thread execution method. Opens the MAG reader, continuously checks
         * for card swipes, and parses track data when a card is detected. Track
         * information is stored in T-L-V (Tag-Length-Value) format where:
         * - Tag (1 byte): 01=track1, 02=track2, 03=track3
         * - Length (1 byte): Length of track data
         * - Value (variable): Track data content
         */
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

                //Analyzing track information with proper bounds checking
                if (allLen > 1) {
                    //track1
                    int len = stripInfo[1] & 0xFF;  // Convert to unsigned
                    if (len > 0 && allLen >= 3 + len) {
                        trackOne.append(" track1: ").append(new String(stripInfo, 2, len));
                        
                        //track2
                        int track2Index = 3 + len;
                        if (allLen > track2Index) {
                            int len2 = stripInfo[track2Index] & 0xFF;  // Convert to unsigned
                            if (len2 > 0 && allLen >= track2Index + 2 + len2) {
                                trackOne.append(" \ntrack2: ").append(new String(stripInfo, track2Index + 1, len2));
                                
                                //track3
                                int track3Index = track2Index + 2 + len2;
                                if (allLen > track3Index) {
                                    int len3 = stripInfo[track3Index] & 0xFF;  // Convert to unsigned
                                    if (len3 > 0 && len3 < 1024 && allLen >= track3Index + 1 + len3) {
                                        trackOne.append(" \ntrack3: ").append(new String(stripInfo, track3Index + 1, len3));
                                    }
                                }
                            }
                        }
                    }

                    //Notify main thread to read information
                    if (trackOne.length() > 0) {
                        trackOne.append("\n");
                        mHandler.removeMessages(MESSAGE_CHECK_FAILE);
                        Message msg = mHandler.obtainMessage(MESSAGE_READ_MAG);
                        Bundle bundle = new Bundle();
                        bundle.putString("track", trackOne.toString());
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
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
