package com.mitdroid.kenshi.mitdroid;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_targets);
        radioGroup = findViewById(R.id.radioGroup);
        targets = getIntent().getStringArrayListExtra("target list");
        MACAddress = getIntent().getStringArrayListExtra("MAC list");

        for(int index = 0; index < targets.size(); index++) {
            if(targets.get(index) != null
                    || !targets.get(index).equals("\n")
                    || !targets.get(index).equals("\t")
                    || targets.get(index).equals("")) {
                final RadioButton radioButton = new RadioButton(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                radioButton.setLayoutParams(layoutParams);
                radioButton.setText(targets.get(index));
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("target ip", radioButton.getText().toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
                radioButtons.add(radioButton);
                radioGroup.addView(radioButtons.get(index));
            }
        }
    }
}
