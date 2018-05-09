package com.mitdroid.kenshi.mitdroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kenshi.networkMapper.optionScan;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;

public class midDroidScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public TextView scanResult;
    public Button button;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toggle;
    public NavigationView navigationView;

    private ArrayList<String> ipAddresses = new ArrayList<>();
    private String log, logName, target;
    private String extension = ".txt";
    private optionScan scanner;
    private boolean format;
    private enum options{scanlocalnetwork, scanspecifictarget, scandetail};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_droid_screen);
        initInstance();
    }

    private void initInstance() {
        scanner = new optionScan(this);
        scanResult = findViewById(R.id.scanResult);
        button = findViewById(R.id.mapNetwork);
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        navigationView = findViewById(R.id.navigationView);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanLocalNetworkItem:
                activateFormat();
                changeString(options.scanlocalnetwork, this);
                break;

            case R.id.scanTargetItem:
                if(target == null)
                    inputTarget();
                changeString(options.scanspecifictarget, this);
                break;

            case R.id.scanDetailItem:
                if(target == null)
                    inputTarget();
                changeString(options.scandetail, this);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setSaveFile();
    }

    /**
     * Description: this function is to quickly displayed the targets in a local network. It will
     * display least information possible for faster scanning. The function should activate another
     * activity and display the targets in that activity.
     *
     * Note: the last ip address is the attacker's ip address
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public void scanLocalNetwork() throws InterruptedException, IOException {
        if(format)
            scanner.initialFormatScan(this);
        else
            scanner.initialScan(this);
        scanResult.setText("Default gateway: " + scanner.getDefaultGateway());
        scanResult.append("Dns 1: " + scanner.getDns1());
        scanResult.append("DNS 2: " + scanner.getDns2());
        scanResult.append("Server address: " + scanner.getServerAdress());
        scanResult.append("Your ip address: " + scanner.getIpAdress());
        scanResult.append("Subnet mask: " + scanner.getNetmask());

        if(format)
            ipAddresses = scanner.splitHosts();
        else
            ipAddresses = scanner.splitIPV4();

        for(int index = 0; index < ipAddresses.size(); index++)
            scanResult.append(ipAddresses.get(index));

        changeScreen();
    }

    private void activateFormat() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Activate format")
                .setMessage("Do you want to display targets in a nice format?" +
                        " (Note: Manufacturer will not be discover)")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        format = true;
                    }
                })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        format = false;
                    }
                })
                .show();
    }

    private void changeScreen() {
        Intent switcher = new Intent(this, displayTarget.class)
                .putExtra("target list", ipAddresses);
        startActivityForResult(switcher, 1);
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

    /**
     * Description
     * @param view
     * @throws IOException
     * @throws InterruptedException
     */

    public void inputTarget(View view) {
        final EditText editText = new EditText(this);
        final String message = "The chosen target: ";
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        editText.setLayoutParams(layoutParams);
        editText.setHint("Enter your target here");
        alertDialog.setView(editText);
        alertDialog.setTitle("Take target")
                .setMessage("Please enter the victim's ip address")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        target = editText.getText().toString();
                        scanResult.setText(message);
                        scanResult.append(target);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void inputTarget() {
        final EditText editText = new EditText(this);
        final String message = "Victim's IP Address: ";
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        editText.setLayoutParams(layoutParams);
        editText.setHint("Enter your target here");
        builder.setView(editText);
        builder.setTitle("Take target")
                .setMessage("Please enter the victim's ip address")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        target = editText.getText().toString();
                        scanResult.setText(message);
                        scanResult.append(target);
                    }
                })
                .show();
    }

    private void click(View button, final Context context, options option) {
        if(option == options.scanlocalnetwork) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        scanLocalNetwork();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else if(option == options.scanspecifictarget) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    while(target == null)
                        inputTarget(view);
                    try {
                        normalScan(target);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        detailScan(target);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void changeString(options option, final Context context) {
        //change the string on the scan button and set the flags
        switch (option) {
            case scanlocalnetwork:
                button.setText(R.string.scanLocalNetwork);
                click(button, context, options.scanlocalnetwork);
                break;

            case scanspecifictarget:
                button.setText(R.string.scanTarget);
                click(button, context, options.scanspecifictarget);
                break;

            case scandetail:
                button.setText(R.string.scanDetail);
                click(button, context, options.scandetail);
                break;
        }
    }

    public void attackButton(View view) {
        //display attack methods
    }

    public void detailScan(String target) throws IOException, InterruptedException {
        scanner.detailScan(this, target);
        log = scanner.getLog();
        scanResult.setText(log);
    }

    public void normalScan(String target) throws IOException, InterruptedException {
        scanner.normalScan(this, target);
        log = scanner.getLog();
        scanResult.setText(log);
    }





    public void setSaveFile() {
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