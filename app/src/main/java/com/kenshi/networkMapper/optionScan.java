package com.kenshi.networkMapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.kenshi.Core.commandProcessor;
import com.kenshi.ThreadsHandler.workRecord;

import org.jetbrains.annotations.Contract;

import java.io.IOException;
import java.util.ArrayList;

import static com.kenshi.networkMapper.stringSplitter.breakdownCustomFlags;

/**
 * @Description: This class will execute options for scanning technique
 * Created by kenshi on 01/02/2018.
 */

public class optionScan {

    private String defaultGateway;
    private String dns1, dns2, serverAdress, ipAddress, netmask;
    private String command = "./nmap ", superCommand = "su -c ./nmap ";
    private workRecord executor;

    public String log;
    public String filename = null;

    private String quickOption = " -sP -n ";
    private ArrayList<String> customFlag;

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
    @NonNull
    @Contract(pure = true)
    private String ipTransformation(int ipAddress) {
        return ((ipAddress & 0xff) + "." +
                ((ipAddress >>>= 8) & 0xff) + "." +
                ((ipAddress >>>= 8) & 0xff) + "." +
                ((ipAddress >>>= 8) & 0xff));
    }

    /**
     * @Description: get the router's general information
     * @param context: The context passed in by the activity
     */
    private void getGatewayInfo(@NonNull Context context) {
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

    public void setCustomFlag(ArrayList<String> flag) { customFlag = flag; }

    /**
     * @Description: quickly scan the local network with a nice format
     * @param context: The context passed in by the activity
     * @throws IOException: input output execption
     * @throws InterruptedException: Interrupted Exception
     */
    public void initialFormatScan(Context context) throws IOException, InterruptedException {
        String formatString = " -oG - ";
        String scanOption = quickOption + defaultGateway + "/24" + formatString;
        log = commandExecution(context, scanOption);
        displayLog();
    }

    /**
     * @Description: quickly scan the network without the format
     * @param context: The context passed in by the activity
     * @throws IOException: input output exception
     * @throws InterruptedException: Interrupted Exception
     */
    public void initialScan(Context context) throws IOException,
            InterruptedException {
        String scanOption = quickOption + defaultGateway + "/24";
        log = commandExecution(context, scanOption);
        displayLog();
    }

    /**
     * @Description: thoroughly scan a specific target
     * @param context: the context passed in by the activity
     * @param targetIp: the target ipv4 address
     * @throws IOException: input output exception
     * @throws InterruptedException: Interrupted Exception
     */
    public void normalScan(Context context, String targetIp) throws IOException,
            InterruptedException {
        log = commandExecution(context, targetIp + " --system-dns -Pn ");
        displayLog();
    }

    /**
     * @Description: Scan every detail information about the target
     * @param context: The context passed in by the activity
     * @param targetIp: The target ipv4 address
     * @throws IOException: input output exception
     * @throws InterruptedException: Interrupted Exception
     */
    public void detailScan(Context context, String targetIp) throws IOException,
            InterruptedException {
        String allOption = " -sV -O -sC -Pn ";
        String scanAll = targetIp + allOption;
        log = commandExecution(context, scanAll);
        displayLog();
    }

    /**
     * @Description: Scan with custom-made flags
     * @param context: The context passed in by the activity
     * @param targetIp: The target ipv4 address
     * @throws IOException: input output exception
     * @throws InterruptedException: Interrupted Exception
     */
    public void customScan(Context context, String targetIp) throws IOException,
            InterruptedException {
        String combination = targetIp + breakdownCustomFlags(customFlag);;
        Log.d("COMBINATION", combination);
        log = commandExecution(context, combination);
        displayLog();
    }

    /**
     * @Description: execute the command
     * @param context: The context passed in by the activity
     * @param option: options passed in
     * @return: the result returned
     * @throws IOException: input output exception
     * @throws InterruptedException: Interrupted Exception
     */
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

    public void displayLog() { Log.d("INFORMATION", log); }
        /*

                String[]binaries = {
                        "nmap",
                        "nmap_os_db",
                        "nmap_payloads",
                        "nmap_protocols",
                        "nmap_rpc",
                        "nmap_service_probes",
                        "nmap_services"
                };

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
