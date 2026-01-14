package com.coldstone.urovocustomerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

    }

    public void onInvokeClick(View view) {
        Intent intent = new Intent(MainActivity.this, InvokeActivity.class);
        startActivity(intent);
    }

    public void onDeviceManagerClick(View view) {
    }

    public void onPiccClick(View view) {
    }

    public void onMagClick(View view) {
    }

    public void onIccClick(View view) {
    }

    public void onScannerClick(View view) {
    }

    public void onPrinterClick(View view) {
    }

    public void onExitClick(View view) {
        finish();
    }

}