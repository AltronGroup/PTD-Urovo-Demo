package com.altron.urovocustomerdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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
        // Use the back button behavior which properly navigates to parent activity
        getOnBackPressedDispatcher().onBackPressed();
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
        long milliseconds = java.lang.System.currentTimeMillis();
        String timeString = Long.toString(milliseconds);
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

        int iAmount;
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
            String errorMsg = e.getMessage() != null ? e.getMessage() : "Unknown error";
            Log.d("InvokeDemo", errorMsg);
        }
    }

    /**
     * Hashes a string using the specified message digest algorithm.
     * Used to generate secure invocation keys for Cendroid authentication.
     *
     * @param type  The hash algorithm type (e.g., "SHA-1", "SHA-256", "SHA-512")
     * @param input The input string to hash
     * @return Hexadecimal string representation of the hash result
     */
    private String hashString(String type,String input) {
        Log.d("ACS", "Hashing: " + input);

        //val HEX_CHARS = "0123456789ABCDEF"
        byte[] bytes = new byte[0];
        try {
            bytes = MessageDigest.getInstance(type).digest(input.getBytes());
        } catch (NoSuchAlgorithmException e) {
            Log.e("InvokeActivity", "Hash algorithm not found: " + type, e);
        }

        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        String messageDigest = sb.toString();

        Log.d("ACS", "Hash result: " + messageDigest);

        return messageDigest;
    }

    /**
     * Called when the Cendroid application returns a result. Parses the response
     * bundle and displays transaction results including all payload data returned
     * by Cendroid.
     *
     * @param requestCode The request code originally supplied to startActivityForResult()
     * @param resultCode  The result code returned by the child activity (RESULT_OK or RESULT_CANCELED)
     * @param data        An Intent that carries the result data (including the Payload bundle)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextView txtView;
        txtView = findViewById(R.id.rxData);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK && data != null){
                StringBuilder sb = new StringBuilder();
                Bundle results = data.getExtras();

                if (results != null) {
                    Bundle payload = results.getBundle("Payload");
                    if (payload != null) {
                        for (String key : payload.keySet()) {
                            Log.d("Bundle Debug", key + " = \"" + payload.get(key) + "\"");
                            sb.append(key).append(" = \"").append(payload.get(key)).append("\"\n");
                        }
                    }
                }
                String sTxt = sb.toString();
                if (!sTxt.isEmpty()) {
                    txtView.setText(sTxt);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                txtView.setText("Transaction cancelled");
            }
        }
    }//onActivityResult
}