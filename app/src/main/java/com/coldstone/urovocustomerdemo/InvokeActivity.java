package com.coldstone.urovocustomerdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class InvokeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_invoke);


        String sToolbarColor  = "#021E4D";

        Spinner spinner = findViewById(R.id.spinner);

        List<String> spinnerList = new ArrayList<>();
        spinnerList.add("Purchase");
        spinnerList.add("Purchase+Cashback");
        spinnerList.add("Cashwithdraw");
        spinnerList.add("Refund");
        spinnerList.add("Cancel");
        spinnerList.add("Balance Enquiry");
        spinnerList.add("Reprint Last Trx");
        spinnerList.add("Reprint List");
        spinnerList.add("Reprint Bank Slip");
        spinnerList.add("Manual Settlement");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

    }

    public void goBack(View view) {
        finish();
    }
    public void executeInvoke(View view) {
        Spinner spinner = findViewById(R.id.spinner);
        String selectedItem = (String) spinner.getSelectedItem();
        String sCommand;
        switch (selectedItem) {
            case "Purchase":
                sCommand = "Sale";
                break;
            case "Purchase+Cashback":
                sCommand = "PurchaseCashback";
                break;
            case "Cashwithdraw":
                sCommand = "CashWithdraw";
                break;
            case "Refund":
                sCommand = "Refund";
                break;
            case "Cancel":
                sCommand = "Cancel";
                break;
            case "Balance Enquiry":
                sCommand = "BalanceEnq";
                break;
            case "Reprint Last Trx":
                sCommand = "Reprint";
                break;
            case "Reprint List":
                sCommand = "ReprintList";
                break;
            case "Reprint Bank Slip":
                sCommand = "ReprintBank";
                break;
            case "Manual Settlement":
                sCommand = "EndOfDay";
                break;
            default:
                sCommand = "Sale";
                break;
        }

        Intent launchIntent =  new Intent();
        launchIntent.setAction("CenDroid");
        launchIntent.putExtra("Operation", sCommand);
        Log.d("invokeDemo","Operation: " + sCommand);
        // Get the time
        Long milliseconds = java.lang.System.currentTimeMillis();
        String timeString = milliseconds.toString();
        launchIntent.putExtra("Time", timeString);
        Log.d("invokeDemo","Time: " + timeString);
        launchIntent.putExtra("Caller", "Caller Name");

        String hashed = hashString("SHA-1",timeString);
        hashed = hashString("SHA-256",hashed);
        hashed = hashString("SHA-512",hashed);

        launchIntent.putExtra("InvocationKey", hashed);
        Log.d("invokeDemo","Invocation Key: " + hashed);
        EditText bAmount = findViewById(R.id.amount);
        EditText bCashAmount = findViewById(R.id.cashback);

        //Use the new Int fields for Amount and Cashback

        int iAmount = 0;
        BigDecimal dParse;
        String amtStr;
        if (bAmount.getText().length() > 0) {
            amtStr = bAmount.getText().toString();
        }
        else {
            amtStr = "0.00";
        }
        dParse = new BigDecimal(amtStr);
        dParse = dParse.multiply(BigDecimal.valueOf(100));
        iAmount = dParse.setScale(0, RoundingMode.HALF_UP).intValue();
        launchIntent.putExtra("IntAmount",iAmount);



        if (bCashAmount.getText().length() > 0) {
            amtStr = bCashAmount.getText().toString();
        }
        else {
            amtStr = "0.00";
        }
        dParse = new BigDecimal(amtStr);
        dParse = dParse.multiply(BigDecimal.valueOf(100));
        iAmount = dParse.setScale(0, RoundingMode.HALF_UP).intValue();
        launchIntent.putExtra("CashbackAmount", iAmount);
        launchIntent.putExtra("CustomHeading","DebiCheck");

        String sTmp;
        EditText bExtraData = findViewById(R.id.extradata);
        if (bExtraData.getText().length() > 0) {
            sTmp = bExtraData.getText().toString();
        }
        else {
            sTmp = "This is an Intent Test";
        }
        launchIntent.putExtra("EcrHostTransfer", sTmp);

        launchIntent.putExtra("Reference","REF# 12345678");

        EditText bLic = findViewById(R.id.license);

        if (bLic.getText().length() > 0) {
            sTmp = bLic.getText().toString();
        }
        else {
            sTmp = "ACS-";
        }
        launchIntent.putExtra("AppName", sTmp);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Log.d("invokeDemo","Invocation Key: ");
        try {
            startActivityForResult(launchIntent, 1);
        } catch (Exception e) {
            Log.d("InvokeDemo",e.getMessage());
        }
    }

    private String hashString(String type,String input) {
        Log.d("ACS", "Hashing: " + input);

        //val HEX_CHARS = "0123456789ABCDEF"
        byte[] bytes = new byte[0];
        try {
            bytes = MessageDigest.getInstance(type).digest(input.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02x",bytes[i]));
        }

        String messageDigest = sb.toString();

        Log.d("ACS", "Hash result: " + messageDigest);

        return messageDigest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView txtView;
        txtView = findViewById(R.id.rxData);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String sTxt = "";
                Bundle results = data.getExtras();

                if (results != null) {

                    Bundle payload = new Bundle();
                    payload = results.getBundle("Payload");
                    for (String key : payload.keySet()) {
                        Log.d("Bundle Debug", key + " = \"" + payload.get(key) + "\"");
                        sTxt += key + " = \"" + payload.get(key) + "\"" + "\n";
                    }
                }
                if (sTxt.isEmpty() == false) {
                    txtView.setText(sTxt);
                }
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}