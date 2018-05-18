package com.kenshi.Plugins.MITM_GUI;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.apache.http.impl.cookie.BasicClientCookie;

import com.mitdroid.kenshi.Main.R;

import java.util.HashMap;

public class HijackerActivity extends AppCompatActivity {

    public static class Session {
        public Bitmap picture = null;
        public String username = null;
        public boolean inited = false;
        public boolean HTTPS = false;
        public String address = null;
        public String domain = null;
        public String useragent = null;
        public HashMap<String, BasicClientCookie> cookies = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hijacker);
    }
}
