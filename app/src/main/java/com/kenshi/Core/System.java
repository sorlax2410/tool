package com.kenshi.Core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;

import com.kenshi.NetworkManager.NetworkChecker;
import com.kenshi.NetworkManager.Target;
import com.kenshi.tools.NMap;
import com.kenshi.Protocol.Proxy.HTTPSRedirector;
import com.kenshi.Protocol.Proxy.Proxy;
import com.kenshi.Protocol.Server.Server;
import com.kenshi.tools.ArpSpoof;
import com.kenshi.tools.Ettercap;
import com.kenshi.tools.Hydra;
import com.kenshi.tools.IPTables;
import com.kenshi.tools.TcpDump;

import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class System {
    private static final String tag = "SYSTEM";
    private static final String errorLogFilename = "mitdroid-debug-error.log";
    private static final String sessionMagic = "MDS";
    private static final Pattern SERVICE_PARSER = Pattern.compile(
            "^([^\\\\s]+)\\\\s+(\\\\d+).*$",
            Pattern.CASE_INSENSITIVE
    );
    public static int HTTP_PROXY_PORT = 8080;
    public static int HTTP_SERVER_PORT = 8081;
    public static int HTTP_REDIR_PORT = 8082;

    public static final String ipv4ForwardFilePath = "/proc/sys/net/ipv4/ip_forward";

    private static boolean initialized = false;
    private static String lastError = "";
    private static String suPath = null;
    private static UpdateManager updateManager = null;
    private static WifiManager.WifiLock wifiLock = null;
    private static PowerManager.WakeLock wakeLock = null;
    private static NetworkChecker network = null;
    private static Vector<com.kenshi.NetworkManager.Target> targets = null;
    private static int currentTarget = 0;
    private static Map<String, String> services = null;
    private static Map<String, String> ports = null;
    private static Map<String, String> vendors = null;
    private static SparseIntArray openPorts = null;

    private static ArrayList<Plugin> plugins = null;
    private static Plugin currentPlugin = null;
    // tools singleton
    private static NMap nmap = null;
    private static ArpSpoof arpSpoof = null;
    private static Ettercap ettercap = null;
    private static IPTables ipTables = null;
    private static Hydra hydra = null;
    private static TcpDump tcpDump = null;

    private static HTTPSRedirector redirector = null;
    private static Proxy proxy = null;
    private static Server server = null;

    private static String storagePath = null;
    private static String sessionName = null;

    private static Object customData = null;

    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    public static String getStoragePath() { return storagePath; }

    public static UpdateManager getUpdateManager() { return updateManager; }

    public void init(Context context) throws Exception {
        this.context = context;
        try {
            storagePath = getSettings().getString("PREF_SAVE_PATH",
                    Environment.getExternalStorageDirectory().toString()
            );
            sessionName = "Mitdroid-session-" + java.lang.System.currentTimeMillis();
            updateManager = new UpdateManager(context);
            plugins = new ArrayList<>();
            targets = new Vector<>();
            openPorts = new SparseIntArray(3);

            //if we are here, network initialization did not throw any error, lock wifi
            WifiManager wifiManager =
                    (WifiManager)context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if(wifiLock == null)
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "WIFI LOCK");
            if(!wifiLock.isHeld())
                wifiLock.acquire();

            //wake lock if enabled
            if(getSettings().getBoolean("PREF_WAKE_LOCK", true)) {
                PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                if(wakeLock == null)
                    wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "WAKE LOCK");
                if(!wakeLock.isHeld())
                    wakeLock.acquire();
            }

            //set ports
            try {
                HTTP_PROXY_PORT = Integer.parseInt(
                        getSettings().getString("PREF_HTTP_PROXY_PORT", "8080")
                );
                HTTP_SERVER_PORT = Integer.parseInt(
                        getSettings().getString("PREF_HTTP_SERVER_PORT", "8081")
                );
                HTTP_REDIR_PORT = Integer.parseInt(
                        getSettings().getString("PREF_HTTP_REDIRECTOR_PORT", "8082")
                );
            } catch (NumberFormatException e) {
                HTTP_PROXY_PORT = 8080;
                HTTP_SERVER_PORT = 8081;
                HTTP_REDIR_PORT = 8082;
            }
            nmap     = new NMap(context);
            arpSpoof = new ArpSpoof(context);
            ettercap = new Ettercap(context);
            ipTables = new IPTables();
            hydra    = new Hydra(context);
            tcpDump  = new TcpDump(context);

            //initialize network data at the end
            network = new NetworkChecker(context);

            Target target = new Target(network),
                    gateway = new Target(network.getGatewayAddress(), network.getGatewayHardware()),
                    device = new Target(network.getLocalAddress(), network.getLocalHardware());
            gateway.setAlias(network.getSSID());
            device.setAlias(android.os.Build.MODEL);

            targets.add(target);
            targets.add(gateway);
            targets.add(device);

            initialized = true;
        } catch(Exception e) { errorLogging(tag, e); throw e; }
    }

    public static String getLibraryPath() {
        return context
                .getFilesDir()
                .getAbsolutePath()
                + "/tools/libs";
    }

    public static SharedPreferences getSettings() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static NetworkChecker getNetwork()
            throws NoRouteToHostException, SocketException { return network; }

    public static void setLastError(String error) { lastError = error; }

    public static synchronized void errorLogging(String tag, Exception e) {
        String message = "Unknown error",
                trace = "Unknown trace",
                filename = (
                        new File(Environment.getExternalStorageDirectory().toString(),
                                errorLogFilename)
                ).getAbsolutePath();
        if(e != null) {
            if(e.getMessage() != null && !e.getMessage().isEmpty())
                message = e.getMessage();
            else if(e.toString() != null)
                message = e.toString();

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);

            e.printStackTrace(printWriter);
            trace = writer.toString();
            if(context != null &&
                    getSettings().getBoolean("PREF_DEBUG_ERROR_LOGGING", false)
                    )
            {
                try {
                    FileWriter fileWriter = new FileWriter(filename, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    bufferedWriter.write(trace);
                    bufferedWriter.close();
                } catch (IOException ioe) {
                    Log.e(tag, ioe.toString());
                }
            }
        }
        setLastError(message);
        Log.e(tag, message);
        Log.e(tag, trace);
    }

    public static void preloadServices() {
        if(services != null | ports != null) {
            try {
                //preload network service map and mac vendors
                services = new HashMap<>();
                ports = new HashMap<>();

                FileInputStream fileInputStream = new FileInputStream(
                        context.getFilesDir().getAbsolutePath() +
                                "tools/nmap/nmap"
                );
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
                String line;
                Matcher matcher;

                while((line = reader.readLine()) != null) {
                    line = line.trim();
                    if((matcher = SERVICE_PARSER.matcher(line)) != null && matcher.find()) {
                        String protocol = matcher.group(1),
                                port = matcher.group(2);
                        services.put(protocol, port);
                        ports.put(protocol, port);
                    }
                }
                dataInputStream.close();
            } catch (Exception e) { errorLogging(tag, e); }
        }
    }

    public static void preloadVendors() {
        if(vendors.isEmpty() && vendors == null) {
            try {
                vendors = new HashMap<>();
                FileInputStream fileInputStream = new FileInputStream(
                        context.getFilesDir().getAbsolutePath() +
                                "/tools/nmap/nmap-mac-prefixes"
                );
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
                String line;

                while(!(line = reader.readLine()).isEmpty() &&
                        (line = reader.readLine()) != null) {
                    line = line.trim();
                    if(!line.startsWith("#") && !line.isEmpty()) {
                        String[]tokens = line.split(" ", 2);
                        if(tokens.length == 2)
                            vendors.put(tokens[0], tokens[1]);
                    }
                }
                dataInputStream.close();

            } catch (Exception e) { errorLogging(tag, e); }
        }
    }

    @Nullable
    public static String getMacVendor(byte[]MAC) {
        preloadVendors();
        if(MAC != null && MAC.length >= 3)
            return vendors.get(String.format("%02X%02X%02X", MAC[0], MAC[1], MAC[2]));
        else
            return null;
    }

    public static String getAppVersionName() {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) { errorLogging(tag, e); }
        return "?";
    }

    @Nullable
    public static String getProtocolByPort(String port) {
        preloadServices();
        return ports.containsKey(port) ? ports.get(port) : null;
    }

    public static int getPortByProtocol(String protocol) {
        preloadServices();
        return services.containsKey(protocol) ? Integer.parseInt(services.get(protocol)) : 0;
    }

    @org.jetbrains.annotations.Contract(pure = true)
    public static Vector<com.kenshi.NetworkManager.Target> getTargets() { return targets; }

    @Contract(pure = true)
    public static com.kenshi.NetworkManager.Target getTarget(int index) { return targets.get(index); }

    @Contract(pure = true)
    public static com.kenshi.NetworkManager.Target getCurrentTarget() { return getTarget(currentTarget); }
}
