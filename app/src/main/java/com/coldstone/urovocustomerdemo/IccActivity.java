package com.coldstone.urovocustomerdemo;

import android.device.IccManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.coldstone.urovocustomerdemo.sdk.UrovoManager;

public class IccActivity extends AppCompatActivity {
    private static final String TAG = "ICCActivity";
    UrovoManager urovoManager;
    IccManager iccManager;

    private EditText mNo;
    private Button mSend;
    private Button mDefApdu;
    private Button mReset;
    private Button mDetect;
    private Button mInitIC;

    EditText mEmission;

    byte[] apduUtf = {
            0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E, 0x53,
            0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_icc);
        urovoManager = UrovoManager.getInstance(this);
        iccManager = urovoManager.getIcc();

        mNo = (EditText) findViewById(R.id.editText1);
        mEmission = (EditText) findViewById(R.id.emission);
        mSend = (Button) findViewById(R.id.button1);
        mSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String reception = mEmission.getText().toString();
                Log.i("debug", "onEditorAction:" + reception);

                if (!reception.equals("")) {
                    if (isHexAnd16Byte(reception)) {
                        mNo.append("SEND: " + reception + "\n");
                        byte[] apdu = hexStringToByteArray(reception);
                        sendCmd(apdu, 1);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "please input content", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDefApdu = (Button) findViewById(R.id.button2);
        mDefApdu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendCmd(apduUtf, 0);
            }
        });

        mReset = (Button) findViewById(R.id.button3);
        mReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                byte[] atr = new byte[64];
                int retLen = iccManager.activate(atr);
                // print the atr
                if (retLen == -1) {
                    mNo.append(" IC Card reset failed......." + "\n");
                } else {
                    mNo.append("ATR: " + bytesToHexString(atr, 0, retLen) + "\n");
                }
            }
        });

        mDetect = (Button) findViewById(R.id.btn_detect);
        mDetect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int status = iccManager.detect();
                if (status != 0) {
                    mNo.append("Please inster IC Card....... : " + status + "\n");
                } else {
                    mNo.append("Card inserted successfully... : " + status + "\n");
                }
            }
        });

        mInitIC = (Button) findViewById(R.id.btn_init_ic);
        mInitIC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int ret = iccManager.open((byte) 0, (byte) 0x01, (byte) 0x01);
                if (ret == 0) {
                    mNo.append("init success : " + ret + "\n");
                } else {
                    mNo.append("init failed : " + ret + "\n");
                }
            }
        });
    }

    private void sendCmd(byte[] cmd, int type) {
        int apduCount = (type == 1) ? cmd.length : apduUtf.length;
        byte[] rspBuf = new byte[256];
        byte[] rspStatus = new byte[2];
        int retLen = iccManager.apduTransmit((type == 1) ? cmd : apduUtf, (char) apduCount, rspBuf, rspStatus);
        if (retLen == -1) {
            mNo.append("APDU RSP REVC: failed  " + retLen + "\n");
            return;
        }
        mNo.append("APDU RSP REVC: " + bytesToHexString(rspBuf, 0, retLen) + "\n");
        mNo.append("APDU RSP REVC Status : " + bytesToHexString(rspStatus, 0, 2) + "\n");
    }

    public boolean isHexAnd16Byte(String hexString) {
        if (hexString.matches("[0-9A-Fa-f]+") == false) {
            // Error, not hex.
            Toast.makeText(getApplicationContext(), "Error: Data must be in hexadecimal(0-9 and A-F)",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        } catch (Exception e) {
            Log.d("debug", "Argument(s) for hexStringToByteArray(String s)"
                    + "was not a hex string");
        }
        return data;
    }

    public static String bytesToHexString(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = offset; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (iccManager != null) {
            int ret = iccManager.deactivate();
            Log.i(TAG, "-----------Eject-----retr=" + ret);
        }
    }
}