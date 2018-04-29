package com.mitdroid.kenshi.mitdroid;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kenshi.networkMapper.optionScan;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class midDroidScreenActivity extends Activity {

    public TextView scanResult;
    public RadioGroup radioGroup;
    public RadioButton scanLocalNetworkButton;
    public RadioButton scanTargetButton;
    public RadioButton detailScanButton;

    private ArrayList<String> ipAddresses = new ArrayList<>();
    private String log, logName, target;
    private String extension = ".txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_droid_screen);
        scanResult = findViewById(R.id.scanResult);
        radioGroup = findViewById(R.id.radioGroup);
        scanLocalNetworkButton = findViewById(R.id.scanLocalNetwork);
        scanTargetButton = findViewById(R.id.scanTarget);
        detailScanButton = findViewById(R.id.scanDetail);
    }


    /**
     * Description: this function is to quickly displayed the targets in a local network. It will
     * display least information possible for faster scanning. The function should activate another
     * activity and display the targets in that activity.
     *
     * Note: the last ip address is the attacker's ip address
     *
     * @param view
     * @throws InterruptedException
     * @throws IOException
     */
    public void scanLocalNetwork(View view) throws InterruptedException, IOException {
        //scan the local network
        optionScan scanner = new optionScan(this);
        scanner.initialScan(this);
        scanResult.setText("Default gateway: " + scanner.getDefaultGateway());
        scanResult.append("Dns 1: " + scanner.getDns1());
        scanResult.append("DNS 2: " + scanner.getDns2());
        scanResult.append("Server address: " + scanner.getServerAdress());
        scanResult.append("Your ip address: " + scanner.getIpAdress());
        scanResult.append("Subnet mask: " + scanner.getNetmask());

        String string = log = scanner.log;
        splitString(string);

        for(int index = 0; index < ipAddresses.size(); index++)
            scanResult.append(ipAddresses.get(index));

        changeScreen();
        setSaveFile(scanner);
    }

    private void changeScreen() {
        Intent switcher = new Intent(this, displayTarget.class)
                .putExtra("target list", ipAddresses);
        startActivityForResult(switcher, 1);
        onStop();
        onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            target = data.getStringExtra("target ip");
            scanResult.setText("The chosen target:\n" + target);
        }
        ipAddresses.clear();
    }

    private void splitString(String string) {
        //split the string
        String[]container = string.split("\\n");
        int limiter = container.length - 2;
        for (int index = 0; index < container.length; index++)
            Log.d("Container Strings: ", container[index]);

        for(int index = 2; index < limiter; index++)
            ipAddresses.add(container[index]);
    }

    public void selectTarget(View view) {
        //TODO: select a displayed targets
    }

    public void displayTargets(View view) {
        //TODO: display scanned targets
    }

    /**
     * Description
     * @param view
     * @param options
     * @throws IOException
     * @throws InterruptedException
     */
/*
    public void inputTarget(View view) {
        //display the text area to input the target
        String ipAddresses = new String();
        ipAddresses = String.valueOf(targetInputSpace.getText());
    }
*/
    public void changeString(View view, int options) throws IOException,
            InterruptedException {
        //change the string on the scan button and set the flags
        switch (options) {
            case 1:
                scanLocalNetwork(view);
                break;

            case 2:
                normalScan(target);
                break;

            case 3:
                detailScan(target);
                break;
        }
    }

    public void scanOtionButton(View view) {
        //display scan option
        radioGroup.setVisibility(View.VISIBLE);
    }

    public void attackButton(View view) {
        //display attack methods
    }

    public void detailScan(String target) throws IOException, InterruptedException {
        optionScan scanner = new optionScan(this);
        scanner.detailScan(this, target);
        log = scanner.getLog();
    }

    public void normalScan(String target) throws IOException, InterruptedException {
        optionScan scanner = new optionScan(this);
        scanner.normalScan(this, target);
        log = scanner.getLog();
    }





    public void setSaveFile(final optionScan scanner) {
        AlertDialog.Builder dialog;
        final EditText editText = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editText.setLayoutParams(layoutParams);
        editText.setHint("File name here");

        dialog = new AlertDialog.Builder(this);
        dialog.setView(editText);
        dialog.setTitle("Save file")
                .setMessage("Do you want to save the log to file?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logName = editText.getText().toString();
                        saveLogs();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        dialog.show();
    }

    public void saveLogs() {
        //saved the captured log
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    this.openFileOutput(logName + extension, Context.MODE_APPEND)
            );
            writer.write(log);
            writer.close();
            File file = this.getDir("files", Context.MODE_PRIVATE);
            Toast.makeText(this,
                    "File " + logName + " saved to " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG)
                    .show();
        }catch(IOException e) {
            Log.e("Exception", "File write fail" + e.toString());
        }
    }
}