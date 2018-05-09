package com.kenshi.networkMapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import com.kenshi.fileHandler.workRecord;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by kenshi on 01/02/2018.
 */

public class optionScan {

    private String defaultGateway;
    private String dns1, dns2, serverAdress, ipAddress, netmask;
    private String command = "su -c ./nmap ";
    private workRecord executor;

    public String log;
    public String filename = null;

    private String quickOption = " -sP -n ";
    private String allOption = " -A ";
    private String formatString = " -oG - ";

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
            ipAddress = ipTransformation(dhcpInfo.ipAddress) + '\n';
            netmask = ipTransformation(dhcpInfo.netmask) + '\n';
        }
    }

    public String getDefaultGateway() { return defaultGateway + "\n"; }
    public String getDns1() { return dns1; }
    public String getDns2() { return dns2; }
    public String getServerAdress() { return serverAdress; }
    public String getIpAdress() { return ipAddress; }
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
        log = commandExecution(context, scanAll);
    }

    @SuppressLint("WrongConstant")
    private String commandExecution(Context context, String option) throws IOException,
            InterruptedException {
        return commandProcessor.runCommand(command + option,
                context.getDir("bin", Context.MODE_MULTI_PROCESS));
    }

    public ArrayList<String>getFormattedTarget() {
        return stringSplitter.splitLine(log);
    }

    public ArrayList<String>getTargets() {
        return stringSplitter.splitIPV4(log);
    }

    public ArrayList<String>getMACAddresses() {
        return stringSplitter.splitManufacturer(log);
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
