package com.mitdroid.kenshi.mitdroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class displayTarget extends Activity {

    public ArrayList<String> targets = new ArrayList<>();
    public RadioGroup radioGroup;
    public ArrayList<RadioButton> radioButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_target);
        radioGroup = findViewById(R.id.radioGroup);
        targets = getIntent().getStringArrayListExtra("target list");
        for(int index = 0; index < targets.size(); index++) {
            RadioButton radioButton = new RadioButton(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(targets.get(index));
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            radioGroup.addView(radioButton);
            radioButtons.add(radioButton);
        }
    }
}
