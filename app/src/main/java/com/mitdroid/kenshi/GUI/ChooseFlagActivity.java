package com.mitdroid.kenshi.GUI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mitdroid.kenshi.mitdroid.R;

public class ChooseFlagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_flag);
    }

    public void chosenFlag(View view) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
    }
}
