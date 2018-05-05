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
    private optionScan holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mid_droid_screen);
        initInstance();
    }

    private void initInstance() {
        scanResult = findViewById(R.id.scanResult);
        button = findViewById(R.id.mapNetwork);
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        NavigationView navigationView = findViewById(R.id.navigationView);

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
                changeString(1);
                Toast.makeText(this,
                        item.getTitle() + " selected",
                        Toast.LENGTH_LONG)
                        .show();
                Log.d("Item test ", item.getTitle() + " selected");
                break;

            case R.id.scanTargetItem:
                changeString(2);
                Toast.makeText(this,
                        item.getTitle() + " selected",
                        Toast.LENGTH_LONG)
                        .show();
                Log.d("Item test ", item.getTitle() + " selected");
                break;

            case R.id.scanDetailItem:
                changeString(3);
                Toast.makeText(this,
                        "nothing selected",
                        Toast.LENGTH_LONG)
                        .show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setSaveFile(holder);
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
        holder = scanner;
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

    private void splitString(String string) {
        //split the string
        String[]container = string.split("\\n");
        int limiter = container.length - 2;
        for (int index = 0; index < container.length; index++)
            Log.d("Container Strings: ", container[index]);

        for(int index = 2; index < limiter; index++)
            ipAddresses.add(container[index]);
    }

    /**
     * Description
     * @param view
     * @throws IOException
     * @throws InterruptedException
     */

    public void inputTarget(View view) {
        //display the text area to input the target
        final EditText editText = new EditText(this);
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
                        scanResult.setText(target);
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

    public void changeString(int options) {
        //change the string on the scan button and set the flags
        switch (options) {
            case 1:
                button.setText(R.string.scanLocalNetwork);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            scanLocalNetwork();
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;

            case 2:
                button.setText(R.string.scanTarget);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            normalScan(target);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;

            case 3:
                button.setText(R.string.scanDetail);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            detailScan(target);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
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