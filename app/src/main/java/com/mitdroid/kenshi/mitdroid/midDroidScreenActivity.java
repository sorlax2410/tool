/*
package com.mitdroid.kenshi.mitdroid;

public class midDroidScreenActivity extends AppCompatActivity
        implements Thread.UncaughtExceptionHandler {

    public TextView scanResult;
    /*
    public RadioGroup radioGroup;
    public RadioButton scanLocalNetworkButton;
    public RadioButton scanTargetButton;
    public RadioButton detailScan;


    private String targetips;
    private String log, logName;
    private String extension = ".txt";

    optionScan scanner = new optionScan(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_droid_screen);

        scanResult = findViewById(R.id.scanResult);
        //radioGroup = findViewById(R.id.radioGroup);
        /*
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

    }
/*
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
    public void scanOtionButton(View view) {
        //display scan option
        //radioGroup.setVisibility(View.VISIBLE);
    }

    public void attackButton(View view) {
        //display attack methods
    }

    public void detailScan() throws IOException, InterruptedException {
        optionScan scanner = new optionScan(this);
        scanner.detailScan(this, targetips);
        log = scanner.getLog();
    }

    public void normalScan() throws IOException, InterruptedException {
        optionScan scanner = new optionScan(this);
        scanner.normalScan(this, targetips);
        log = scanner.getLog();
    }

    public void saveLogs() {
        //saved the captured log
        try {
                OutputStreamWriter writer = new OutputStreamWriter(
                        this.openFileOutput(
                        logName + extension, Context.MODE_APPEND)
                );
                writer.write(log);
                writer.close();
        }catch(IOException e) {
            Log.e("Exception", "File write fail" + e.toString());
        }
    }
}

*/

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

import com.kenshi.networkMapper.optionScan;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class midDroidScreenActivity extends Activity {

    public TextView scanResult;
    //public EditText targetInputSpace;
    private String[] targetips;
    private String log, logName;
    private String extension = ".txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_droid_screen);
        scanResult = findViewById(R.id.scanResult);
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

        String string = scanner.log;
        String[]container = string.split("\\n");
        int limiter = container.length - 2;
        int counter = 0;
        targetips = new String[--limiter];
        Log.d("Index limit: ", String.valueOf(limiter));
        Log.d("Actual splited number: ", String.valueOf(container.length));

        for (int index = 0; index < container.length; index++)
            Log.d("Container Strings: ", container[index]);

        for(int index = 1; index < limiter; index++) {
            targetips[counter] = container[index];
            scanResult.append(targetips[counter]);
            counter++;
        }

        setSaveFile(scanner);
/*
        Intent switcher = new Intent(this, displayTarget.class)
                .putExtra("target list", targetips);
        startActivity(switcher);
*/
    }

    public void selectTarget(View view) {
        //TODO: select a displayed targets
    }

    public void displayTargets(View view) {
        //TODO: display scanned targets
    }

    /*
    public void inputTarget(View view) {
        //display the text area to input the target
        String targetips = new String();
        targetips = String.valueOf(targetInputSpace.getText());
    }
*/
    public void scanOtionButton(View view) {
        //display scan option
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

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
            dialog = new AlertDialog.Builder(this, android.R.style.Theme);
        else
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
    }

    public void saveLogs() {
        //saved the captured log
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    this.openFileOutput(logName + extension, Context.MODE_APPEND)
            );
            writer.write(log);
            writer.close();
        }catch(IOException e) {
            Log.e("Exception", "File write fail" + e.toString());
        }
    }
}