package com.mitdroid.kenshi.GUI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;

import com.mitdroid.kenshi.mitdroid.R;

import java.util.ArrayList;

public class ChooseFlagActivity extends AppCompatActivity {

    private ArrayList<CheckBox>flags = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_flag);
    }
}
