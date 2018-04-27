package com.mitdroid.kenshi.mitdroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class displayTarget extends AppCompatActivity {

    public String targets;
    public RadioGroup radioGroup;
    public ArrayList<RadioButton> radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_target);
        radioGroup = findViewById(R.id.radioGroup);
        targets = getIntent().getStringExtra("target list");
    }
}
