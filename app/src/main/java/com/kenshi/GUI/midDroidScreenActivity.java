package com.kenshi.GUI;

import android.annotation.SuppressLint;
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

import com.kenshi.GUI.displayFormattedTargets;
import com.kenshi.GUI.displayTargets;
import com.kenshi.networkMapper.optionScan;
import com.mitdroid.kenshi.mitdroid.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Objects;

public class midDroidScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public TextView scanResult;
    public Button button;
    public Button attackButton;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toggle;
    public NavigationView navigationView;

    private ArrayList<String> FormattedIpAddresses = new ArrayList<>();
    private ArrayList<String> ipAddresses = new ArrayList<>();
    private ArrayList<String> MACAddress = new ArrayList<>();
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

    /**
     *
     */
    private void initInstance() {
        scanner = new optionScan(this);
        scanResult = findViewById(R.id.scanResult);
        button = findViewById(R.id.mapNetwork);
        drawerLayout = findViewById(R.id.drawerLayout);
        attackButton = findViewById(R.id.attackButton);
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

    /**
     * Description: displaying navigation menu when the hamburger icon is selected
     * @param item: The hamburger item on the top right
     * @return: return true if selected
     */
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
                changeString(options.scanlocalnetwork);
                break;

            case R.id.scanTargetItem:
                if(target == null)
                    inputTarget();
                changeString(options.scanspecifictarget);
                break;

            case R.id.scanDetailItem:
                if(target == null)
                    inputTarget();
                changeString(options.scandetail);
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
     * @throws InterruptedException: exit when an interruption is thrown
     * @throws IOException: exit when an input/output exception is thrown
     */
    @SuppressLint("SetTextI18n")
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

        if(format) {
            FormattedIpAddresses = scanner.getFormattedTarget();
            changeFormattedScreen();
        }
        else {
            ipAddresses = scanner.getTargets();
            MACAddress = scanner.getMACAddresses();
            changeScreen();
        }
        for(int index = 0; index < ipAddresses.size(); index++)
            scanResult.append(ipAddresses.get(index));
    }

    /**
     * Decsription: activate the format when scanLocalNetwork item is chosen within the navigation
     * bar
     */
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

    /**
     * Description: change to another activity to choose a target
     */
    private void changeScreen() {
        Intent switcher = new Intent(this, displayTargets.class)
                .putExtra("target list", ipAddresses)
                .putExtra("MAC list", MACAddress);
        startActivityForResult(switcher, 2);
    }

    /**
     * Description: change to the formatted activity to choose a target
     */
    private void changeFormattedScreen() {
        Intent switcher = new Intent(this, displayFormattedTargets.class)
                .putExtra("Formatted target list", FormattedIpAddresses);
        startActivityForResult(switcher, 1);
    }

    /**
     * Description: get the victim's ip address when the other activity is destroyed
     * @param requestCode: the requested code from the other activity
     * @param resultCode: the result code the other activity returns
     * @param data: the data returned by the other activity
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            target = data.getStringExtra("target ip");
            scanResult.setText("The chosen target:\n" + target);
            FormattedIpAddresses.clear();
        }
        else if(requestCode == 2 && resultCode == RESULT_OK) {
            target = data.getStringExtra("target ip");
            scanResult.setText("The chosen target:\n" + target);
            ipAddresses.clear();
        }
    }

    /**
     * Description: display the target input box for user
     * @param view: the pressed button
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

    /**
     * Description: just like the above method but other function
     */
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

    /**
     * Description: the click function display various methods when a specific option is chosen
     * @param button: a pressed button
     * @param option: the chosen option
     */
    private void click(View button, options option) {
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

    /**
     * Description: change the string on the scan button and set the flags
     * @param option: the chosen option
     */
    public void changeString(options option) {
        switch (option) {
            case scanlocalnetwork:
                button.setText(R.string.scanLocalNetwork);
                click(button, options.scanlocalnetwork);
                break;

            case scanspecifictarget:
                button.setText(R.string.scanTarget);
                click(button, options.scanspecifictarget);
                break;

            case scandetail:
                button.setText(R.string.scanDetail);
                click(button, options.scandetail);
                break;
        }
    }

    public void attackButton(View view) {
        //display attack methods
    }

    /**
     * Description: scan a specific target carefully(including displaying it's OS)
     * @param target: the chosen target(must not be null)
     * @throws IOException: an input/output exception
     * @throws InterruptedException: an interrupted exception(another button is pressed while
     * processing)
     */
    public void detailScan(@NonNull String target) throws IOException, InterruptedException {
        scanner.detailScan(this, target);
        log = scanner.getLog();
        scanResult.setText(log);
    }

    /**
     * Description: scan a target
     * @param target: the chosen target
     * @throws IOException: an input/output exception
     * @throws InterruptedException: an interrupted exception(another button is pressed while
     * processing)
     */
    public void normalScan(String target) throws IOException, InterruptedException {
        scanner.normalScan(this, target);
        log = scanner.getLog();
        scanResult.setText(log);
    }


    /**
     * Description: designing the file saving box and adding the save function
     */

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

    /**
     * Description: save the captured log to the root data directory
     */

    public void saveLogs() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    this.openFileOutput(logName + extension, Context.MODE_APPEND)
            );
            writer.write(log);
            writer.close();
            File file = getFileStreamPath(logName + extension);
            Toast.makeText(this,
                    "File " + logName + " saved to " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG)
                    .show();
        }catch(IOException e) {
            Log.e("Exception", "File write fail" + e.toString());
        }
    }
}