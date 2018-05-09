package com.mitdroid.kenshi.mitdroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class displayTargets extends AppCompatActivity {

    public ArrayList<String> targets = new ArrayList<>();
    public ArrayList<String> MACAddress = new ArrayList<>();
    public RadioGroup radioGroup;

    public ArrayList<RadioButton>radioButtons = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_targets);
        radioGroup = findViewById(R.id.radioGroup);

        /**
         * Description: get the passed lists
         */

        targets = getIntent().getStringArrayListExtra("target list");
        MACAddress = getIntent().getStringArrayListExtra("MAC list");

        /**
         * Description: attaching lists to buttons
         */

        for(int index = 0; index < targets.size(); index++) {
            final RadioButton radioButton = new RadioButton(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            radioButton.setLayoutParams(layoutParams);
            radioButton.setText(targets.get(index) + " " + MACAddress.get(index));
            final int finalIndex = index;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("target ip", targets.get(finalIndex));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            radioButtons.add(radioButton);
            radioGroup.addView(radioButtons.get(index));
        }
    }
}
