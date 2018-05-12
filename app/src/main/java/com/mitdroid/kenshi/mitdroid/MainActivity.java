package com.mitdroid.kenshi.mitdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kenshi.GUI.scanningActivities.midDroidScreenActivity;
import com.kenshi.Core.netInstaller;

import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String successMsg = "The program will now run. Please wait";
    private String failedMsg = "If your phone is not rooted please do it now. If it is, please provide root permission for this app";

    private String firstStartPreference = "startPreference";
    private String debugTag = "debugTag";

    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkConnection(this);
        testRoot(this);
        firstInstall(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void changeScreen(View view) {
        //change to the middroid screen
        Intent changer = new Intent(view.getContext(), midDroidScreenActivity.class);
        startActivity(changer);
    }

    private void firstInstall(Context context) {
        boolean firstInstall;
        String sharedObject = "sharedObject";
        final SharedPreferences sharedPreferences = context.getSharedPreferences(sharedObject,
                Context.MODE_MULTI_PROCESS);
        firstInstall = sharedPreferences.getBoolean(firstStartPreference, true);

        if(firstInstall) {
            final netInstaller installer = new netInstaller(context.getApplicationContext());
            installer.installResources();
            Log.d(debugTag, "installing binaries");
            sharedPreferences.edit().putBoolean(firstStartPreference, false).apply();
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
                    public void onClick(DialogInterface dialog, int which) {}
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
                    public void onClick(DialogInterface dialogInterface, int i) {}
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
                    public void onClick(DialogInterface dialogInterface, int i) { onDestroy(); }
                })
                .show();
    }

    private void checkConnection(Context context) {
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (int i = 0; i < networkInfo.length; i++)
            if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                Toast.makeText(context, "you are connected", Toast.LENGTH_SHORT)
                        .show();
    }

}
