package com.mitdroid.kenshi.mitdroid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kenshi.networkMapper.optionScan;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class midDroidScreenActivity extends AppCompatActivity
        implements Thread.UncaughtExceptionHandler {

    public TextView scanResult;
    public EditText targetInputSpace;
    public RadioGroup radioGroup;
    public RadioButton scanLocalNetwork;
    public RadioButton scanTarget;
    public RadioButton detailScan;

    private String targetip;
    private String log, logName;
    private String pcap = ".pcap";

    optionScan scanner = new optionScan(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_droid_screen);
        //scanResult = findViewById(R.id.scanResult);
        radioGroup = findViewById(R.id.radioGroup);
        scanLocalNetwork = findViewById(R.id.scanLocalNetwork);
        scanTarget = findViewById(R.id.scanTarget);
        detailScan = findViewById(R.id.scanDetail);
/*
        scanResult.setText("Default gateway: " + scanner.getDefaultGateway());
        scanResult.append("Dns 1: " + scanner.getDns1());
        scanResult.append("Dns 2: " + scanner.getDns2());
        scanResult.append("Server address: " + scanner.getServerAdress());
        scanResult.append("Your ip address: " + scanner.getIpAdress());
        scanResult.append("Subnet mask: " + scanner.getNetmask());
*/
    }

    public void changeString(View view, int options) throws IOException,
            InterruptedException {
        //change the string on the scan button and set the flags
        switch (options) {
            case 1:
                scanLocalNetwork();
                break;

            case 2:
                normalScan();
                break;

            case 3:
                detailScan();
                break;
        }
    }

    public void scanLocalNetwork() throws InterruptedException, IOException {
        //scan the local network
        scanner.initialScan(this);
        //Log.d("Log: ", scanner.log);
        //scanResult.setText(scanner.log);
        Intent switcher = new Intent(this, displayTarget.class)
                .putExtra("target list", scanner.log);
        startActivity(switcher);
    }

    public void inputTarget(View view) {
        //display the text area to input the target
        targetip = String.valueOf(targetInputSpace.getText());
    }

    public void scanOtionButton(View view) {
        //display scan option
        radioGroup.setVisibility(View.VISIBLE);
    }

    public void attackButton(View view) {
        //display attack methods
    }

    public void detailScan() throws IOException, InterruptedException {
        optionScan scanner = new optionScan(this);
        scanner.detailScan(this, targetip);
        log = scanner.showLog();
    }

    public void normalScan() throws IOException, InterruptedException {
        optionScan scanner = new optionScan(this);
        scanner.normalScan(this, targetip);
        log = scanner.showLog();
    }

    public void saveLogs(Context context) {
        //saved the captured log
        try {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(logName + pcap, Context.MODE_APPEND));
            writer.write(log);
            writer.close();
        }catch(IOException e) {
            Log.e("Exception", "File write fail" + e.toString());
        }
    }


    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
