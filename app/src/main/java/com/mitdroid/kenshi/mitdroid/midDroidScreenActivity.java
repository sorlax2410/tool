package com.mitdroid.kenshi.mitdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kenshi.networkMapper.netInstaller;
import com.kenshi.networkMapper.optionScan;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class midDroidScreenActivity extends AppCompatActivity {

    public TextView scanResult;
    public EditText targetInputSpace;

    private String successMsg = "The program will now run. Please wait";
    private String failedMsg = "If your phone is not rooted please do it now. If it is, please provide root permission for this app";
    private String targetip;
    private String log, logName;
    private String pcap = ".pcap";
    private String sharedObject = "sharedObject";
    private String firstStartPreference = "startPreference";
    private String debugTag = "debugTag";

    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_droid_screen);
        scanResult = findViewById(R.id.scanResult);

        checkConnection(this);
        testRoot(this);
        firstInstall(this);
    }

    public void scanLocalNetwork(View view) {
        //scan the local network
        optionScan scanner = new optionScan(this);
        scanner.initialScan(this);
        scanResult.setText(scanner.log);
        scanResult.append("Default gateway: " + scanner.getDefaultGateway());
        scanResult.append("Dns 1: " + scanner.getDns1());
        scanResult.append("Dns 2: " + scanner.getDns2());
        scanResult.append("Server address: " + scanner.getServerAdress());
        scanResult.append("Your ip address: " + scanner.getIpAdress());
        scanResult.append("Subnet mask: " + scanner.getNetmask());
    }

    public void inputTarget(View view) {
        //display the text area to input the target
        targetip = String.valueOf(targetInputSpace.getText());
    }

    public void scanOtionButton(View view) {
        //display scan option
    }

    public void attackButton(View view) {
        //display attack methods
    }

    private void checkConnection(Context context) {
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (int i = 0; i < networkInfo.length; i++)
            if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                Toast.makeText(context, "you are connected", Toast.LENGTH_SHORT)
                        .show();
    }

    public void detailScan() {
        optionScan scanner = new optionScan(this);
        scanner.detailScan(this, targetip);
        log = scanner.showLog();
    }

    public void normalScan() {
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


    private void firstInstall(Context context) {
        boolean firstInstall = true;
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedObject,
                Context.MODE_MULTI_PROCESS);
        firstInstall = sharedPreferences.getBoolean(firstStartPreference, true);

        if(firstInstall) {
            final netInstaller installer = new netInstaller(context.getApplicationContext());
            installer.installResources();
            Log.d(debugTag, "installing binaries");
            sharedPreferences.edit().putBoolean(firstStartPreference, false).commit();
        }
        else
            binInstalledMessage(context);
    }

    private void binInstalledMessage(Context context) {
        AlertDialog.Builder builder;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(context);
        builder.setTitle("Notifier")
                .setMessage("The binary is already installed")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Fix the problem of displaying multiple times while rotate the screen
                    }
                })
                .show();
    }

    private void testRoot(Context context) {
        try {
            Process process = Runtime.getRuntime().exec("su -c");
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes("echo \"Do I have root\" > /system/sd/temporary.txt");
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try{
                process.waitFor();
                if(process.exitValue() != 255)
                    successMessage(context);
                else
                    failedMessage(context);
            }catch(InterruptedException e) {
                failedMessage(context);
            }
        }catch(IOException e) {
            failedMessage(context);
        }
    }

    private void successMessage(Context context) {
        AlertDialog.Builder dialog;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        else
            dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Notifier")
                .setMessage(successMsg)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: fix the problem of display multiple times while rotate the screen
                    }
                })
                .show();
    }

    private void failedMessage(Context context) {
        AlertDialog.Builder dialog;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        else
            dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Notifier")
                .setMessage(failedMsg)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //turn off the program
                        //TODO: make the program turned off after ok button is pressed
                    }
                })
                .show();
    }
}
