package com.kenshi.networkMapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.kenshi.fileHandler.workRecord;

import java.io.File;
import java.io.IOException;

/**
 * Created by kenshi on 01/02/2018.
 */

public class optionScan {

    private String defaultGateway;
    private String dns1, dns2, serverAdress, ipAdress, netmask;
    private String command = "./nmap ";
    private workRecord executor;

    public String log;

    private String quickOption = "-sP";
    private String allOption = "-A";

    public optionScan(Context context) {
        //initial scan
        getGatewayInfo(context);
        //initialScan(context);
    }

    /**
     * General: Transforming gathered dhcp information into ip addresses
     * Detail description: This function is used because dhcp return the integer value converted
     * from 8 bits shifted binaries
     * @param ipAddress the converted integer value
     * @return the converted bits value and change it into string of ip address
     */
    private String ipTransformation(int ipAddress) {
        return ((ipAddress & 0xff) + "." +
                ((ipAddress >>>= 8) & 0xff) + "." +
                ((ipAddress >>>= 8) & 0xff) + "." +
                ((ipAddress >>>= 8) & 0xff));
    }

    private void getGatewayInfo(Context context) {
        DhcpInfo dhcpInfo = null;
        WifiManager wifiManager = (WifiManager)context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null)
            dhcpInfo = wifiManager.getDhcpInfo();

        if (dhcpInfo != null) {
            defaultGateway = ipTransformation(dhcpInfo.gateway);
            dns1 = ipTransformation(dhcpInfo.dns1) + '\n';
            dns2 = ipTransformation(dhcpInfo.dns2) + '\n';
            serverAdress = ipTransformation(dhcpInfo.serverAddress) + '\n';
            ipAdress = ipTransformation(dhcpInfo.ipAddress) + '\n';
            netmask = ipTransformation(dhcpInfo.netmask) + '\n';
        }
    }

    public String getDefaultGateway() { return defaultGateway + "\n"; }
    public String getDns1() { return dns1; }
    public String getDns2() { return dns2; }
    public String getServerAdress() { return serverAdress; }
    public String getIpAdress() { return ipAdress; }
    public String getNetmask() { return netmask; }

    public String showLog() { return log; }

    public void initialScan(Context context) {
        //quick scan the local network
        String scanOption = quickOption + " " + defaultGateway + "/24";
        new AsyncCommandExecutor(context).execute(command + scanOption);

    }

    public void normalScan(Context context, String targetIp) {
        new AsyncCommandExecutor(context).execute(command + targetIp);
    }

    public void detailScan(Context context, String targetIp) {
        String scanAll = targetIp + allOption;
        new AsyncCommandExecutor(context).execute(command + scanAll);
    }

    private void commandExecution(Context context) {
        //TODO: execute commands passed in
    }

    /**
     * Note: Asyntask class is used only for publishing results to the screen and cannot manipulate
     * threads and/or handlers
     *
     * Moreover this kind of code will lead to crashing the program when the thread is no longer
     * valid in the View when the view is destroyed(when users rotate the screen
     * or hit the back button) which is a common error in handling threads
     *
     * Note: don't hold references to any type of UI objects to any kind of threading scenario
     * The solution for updating the UI from threaded work is to force the top level activity
     * or fragment to be the sole system that responsible for updating the UI object
     */
    //TODO: use another class instead of asynctask
    //TODO: make a work record that pairs the views and the update functions
    public class AsyncCommandExecutor extends AsyncTask<String, Void, Void> {

        public String returnOutput, debugTag;
        public File binary;

        String[]binaries = {
                "nmap",
                "nmap_os_db",
                "nmap_payloads",
                "nmap_protocols",
                "nmap_rpc",
                "nmap_service_probes",
                "nmap_services"
        };

        @SuppressLint("WrongConstant")
        public AsyncCommandExecutor(Context context) {
            this.binary = context.getDir("bin", Context.MODE_MULTI_PROCESS);
            this.debugTag = "debugTag";
        }
/*
        private void display(String[] parameter) {
            try {
                String workingDirectory = commandProcessor.runCommand("pwd",
                        binary.getAbsoluteFile());
                Log.d("Working directory", binary.getAbsolutePath());
                Log.d("Path", workingDirectory);
            }
            catch(IOException e) { Log.d(debugTag, e.getMessage()); }
            catch(InterruptedException e) { Log.d(debugTag, e.getMessage()); }

            for(int i = 0; i < binaries.length; i++) {
                try {
                    String listDetail = commandProcessor.runCommand("ls -la " + binaries[i],
                            binary.getAbsoluteFile());

                    Log.d("Listing Tag", "ls -la output: " + listDetail);
                }
                catch(IOException e) { Log.d(debugTag, e.getMessage()); }
                catch(InterruptedException e) { Log.d(debugTag, e.getMessage()); }
            }

            for(int i = 0; i < parameter.length; i++) { Log.d("Parameters", parameter[i]); }
        }

        private void checkFile(String path) {
            File checker = new File(path);
            Log.d("Path", checker.getAbsolutePath());
            if (checker.exists())
                Log.d("Path", "Path available !!");

            for(int i = 0; i < binaries.length; i++) {
                checker = new File(path, binaries[i]);
                Log.d("Path", checker.getAbsolutePath());
                if (checker.exists())
                    Log.d("Path", "Path available !!");
            }
        }
*/
        @Override
        protected Void doInBackground(String... params) {
            try {
                returnOutput = commandProcessor.runCommand(params[0], binary.getAbsoluteFile());
            } catch (IOException e) {
                returnOutput = "IOException while trying to scan";
                Log.d(debugTag, e.getMessage());
            } catch (InterruptedException e) {
                returnOutput = "Nmap scan interrupted";
                Log.d(debugTag, e.getMessage());
            }
            log = returnOutput;
            Log.d("Command", log);
            Log.d("Command", returnOutput);
            return null;
        }
    }

}
