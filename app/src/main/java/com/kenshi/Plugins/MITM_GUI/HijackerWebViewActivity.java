package com.kenshi.Plugins.MITM_GUI;

import android.app.Activity;
import android.os.Bundle;

import com.mitdroid.kenshi.Main.R;

public class HijackerWebViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hijacker_web_view);
    }
}
