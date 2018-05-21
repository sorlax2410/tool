package com.kenshi.Plugins.MITM_GUI;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.mitdroid.kenshi.Main.R;

public class PasswordSnifferActivity extends SherlockActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_sniffer);
    }
}
