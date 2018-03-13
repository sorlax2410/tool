package com.mitdroid.kenshi.mitdroid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void changeScreen(View view) {
        //change to the middroid screen
        Intent changer = new Intent(view.getContext(), midDroidScreenActivity.class);
        startActivity(changer);
    }

}
