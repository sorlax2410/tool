package com.kenshi.networkMapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.kenshi.fileHandler.workRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by kenshi on 01/02/2018.
 */

public class optionScan {

    private String defaultGateway;
    private String dns1, dns2, serverAdress, ipAdress, netmask;
    private String command = "su -c ./nmap ";
    private workRecord executor;

    public String log;
    public String filename = null;
    public ArrayList<String> targets = new ArrayList<>();

    private String quickOption = " -sP -n ";
    private String allOption = " -A ";
    private String formatString = " -oG - ";

    private boolean saveLog = false;

    public optionScan(Context context) {
        //initial scan
        getGatewayInfo(context);
    }

    //public ~optionScan() { targets.trimToSize(); targets.clear(); }

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
    public String getLog() { return log; }

    public void initialFormatScan(Context context) throws IOException, InterruptedException {
        String scanOption = quickOption + defaultGateway + "/24" + formatString;
        log = commandExecution(context, scanOption);
    }

    public void initialScan(Context context) throws IOException,
            InterruptedException {
        String scanOption = quickOption + defaultGateway + "/24";
        log = commandExecution(context, scanOption);
    }

    public void normalScan(Context context, String targetIp) throws IOException,
            InterruptedException {
        log = commandExecution(context, targetIp);
    }

    public void detailScan(Context context, String targetIp) throws IOException,
            InterruptedException {
        String scanAll = targetIp + allOption;
        log = commandExecution(context, allOption);
    }

    @SuppressLint("WrongConstant")
    private String commandExecution(Context context, String option) throws IOException,
            InterruptedException {
        return commandProcessor.runCommand(command + option,
                context.getDir("bin", Context.MODE_MULTI_PROCESS));
    }

    private void splitLine() {
        String[]container = log.split("\\n");
        int limiter = container.length - 2;

        for(int index = 2; index < limiter; index++)
            targets.add(container[index]);
    }

    private void splitTab() {
        splitLine();
        String container = targets.toString();
        targets.trimToSize();
        targets.clear();
        String[]targets = container.split("\\t");
        boolean meetTab;

        for(int index = targets.length; index > -1; index++) {
            if(checkStatus(Arrays.toString(targets)))
                meetTab = true;
            else
                meetTab = false;
            if(meetTab)
                this.targets.add(targets[index - 1]);
        }
    }

    public ArrayList<String> splitHosts() {
        splitTab();
        String string = targets.toString();
        targets.trimToSize();
        targets.clear();
        String[]targets = string.split("Host: ");

        for(int index = 0; index < targets.length; index++)
            if(!targets[index].equals("Host: "))
                this.targets.add(targets[index]);

        for(int index = 0; index < this.targets.size(); index++)
            Log.d("splitter host test", this.targets.get(index));
        return this.targets;
    }

    private boolean checkStatus(String status) {
        if(status.equals("Status: UP"))
            return true;
        return false;
    }

    public ArrayList<String> splitIPV4() {
        String[]string = log.split("Nmap scan report for ");

        for(int index = 1; index < string.length; index++)
            targets.add(string[index]);

        string = targets.toString().split("\\n");
        targets.trimToSize();
        targets.clear();

        for(int index = 0; index < string.length - 4; index++)
            if(index % 3 == 0)
                targets.add(string[index]);

        String replacement;
        replacement = targets.toString()
                .replaceAll("\\[", "")
                .replaceAll("]", "");
        string = replacement.split(", ");
        targets.trimToSize();
        targets.clear();

        for(int index = 0; index < string.length; index++)
            if(!string[index].equals(""))
                targets.add(string[index]);
        return targets;
    }

    public ArrayList<String> splitManufacturer() {
        ArrayList<String>container = new ArrayList<>();
        String[]string = log.split("MAC Address:");

        for(int index = 0; index < string.length; index++)
            if(index % 2 == 0)
                container.add(string[index]);

        string = container.toString().split("\\n");
        container.trimToSize();
        container.clear();
        for(int index = 0; index < string.length; index++)
            if(index % 2 != 0)
                container.add(string[index]);

        string = container.toString().split("\\p{P}");
        container.trimToSize();
        container.clear();
        for(int index = 0; index < string.length; index++)
            if(index % 2 == 0)
                container.add(string[index]);

        string = container.toString().split("\\p{P}");
        container.trimToSize();
        container.clear();
        for(int index = 0; index < string.length; index++)
            if(index % 2 != 0)
                container.add(string[index]);

        for(int index = 0; index < container.size(); index++)
            Log.d("Split manufacturer test", container.get(index));
        return container;
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
/*    public class AsyncCommandExecutor extends AsyncTask<String, Void, Void> {

        public String returnOutput, debugTag;
        public File binary;

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
*/
}
