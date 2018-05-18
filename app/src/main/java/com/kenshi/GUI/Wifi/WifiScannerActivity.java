package com.kenshi.GUI.Wifi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mitdroid.kenshi.Main.R;

public class WifiScannerActivity extends AppCompatActivity {

    private static final String tag = "WIFI SCANNER";

    public static final String CONNECTED = "WifiScannerActivity.CONNECTED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_scanner);
    }
}
