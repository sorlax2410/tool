package com.kenshi.Plugins.MITM_GUI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mitdroid.kenshi.Main.R;

public class PasswordSnifferActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_sniffer);
    }
}
