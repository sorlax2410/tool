package com.kenshi.Core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;

import com.kenshi.GUI.Wifi.WifiScannerActivity;
import com.kenshi.Network.NetworkManager.NetworkChecker;
import com.kenshi.Network.NetworkManager.Target;
import com.kenshi.Plugins.MITM_GUI.HijackerActivity;
import com.kenshi.tools.NMap;
import com.kenshi.Network.Protocol.Proxy.HTTPSRedirector;
import com.kenshi.Network.Protocol.Proxy.Proxy;
import com.kenshi.Network.Protocol.Server.Server;
import com.kenshi.tools.ArpSpoof;
import com.kenshi.tools.Ettercap;
import com.kenshi.tools.Hydra;
import com.kenshi.tools.IPTables;
import com.kenshi.tools.TcpDump;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public class System {
    private static final String tag = "SYSTEM";
    private static final String ERROR_LOG_FILENAME = "mitdroid-debug-error.log";
    private static final String SESSION_MAGIC = "MDS";
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
    private static NetworkChecker networkChecker = null;
    private static Vector<com.kenshi.Network.NetworkManager.Target> targets = null;
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

    public static String getSuPath() {
        if(suPath != null && !suPath.isEmpty())
            return suPath;
        try {
            Process process = Runtime.getRuntime().exec("which su");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            String line = null;

            while((line = bufferedReader.readLine()) != null) {
                if(!line.isEmpty() && line.startsWith("/")) {
                    suPath = line;
                    break;
                }
            }

            return suPath;
        } catch (Exception e) { errorLogging(tag, e); }

        return "su";
    }

    @Contract(pure = true)
    public static ArpSpoof getArpSpoof() { return arpSpoof; }

    @Contract(pure = true)
    public static Ettercap getEttercap() { return ettercap; }

    @Contract(pure = true)
    public static Hydra getHydra() { return hydra; }

    @Contract(pure = true)
    public static TcpDump getTcpDump() { return tcpDump; }

    public static InputStream getRawResource(int id) {
        return context.getResources().openRawResource(id);
    }

    @Contract(pure = true)
    public static Object getCustomData() { return customData; }

    @Contract(pure = true)
    public static String getStoragePath() { return storagePath; }

    @Contract(pure = true)
    public static UpdateManager getUpdateManager() { return updateManager; }

    @NonNull
    public static String getLibraryPath() {
        return context
                .getFilesDir()
                .getAbsolutePath()
                + "/tools/libs";
    }

    public static SharedPreferences getSettings() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Contract(pure = true)
    public static NetworkChecker getNetwork()
            throws NoRouteToHostException, SocketException { return networkChecker; }

    @Nullable
    public static String getMacVendor(byte[]MAC) {
        preloadVendors();
        if(MAC != null && MAC.length >= 3)
            return vendors.get(String.format("%02X%02X%02X", MAC[0], MAC[1], MAC[2]));
        else
            return null;
    }

    public static Proxy getProxy() {
        try {
            if(proxy == null)
                proxy = new Proxy(getNetwork().getLocalAddress(), HTTP_PROXY_PORT);
        } catch (Exception e) { errorLogging(tag, e); }

        return proxy;
    }

    public static String getGatewayAddress() {
        return networkChecker.getGatewayAddress().getHostAddress();
    }

    public static IPTables getIpTables() { return ipTables; }

    public static Server getServer() {
        try {
            if(server == null)
                server = new Server(getNetwork().getLocalAddress(), HTTP_SERVER_PORT);
        } catch (Exception e) { errorLogging(tag, e); }

        return server;
    }

    public static HTTPSRedirector getHttpsRedirector() {
        try {
            if(redirector == null)
                redirector = new HTTPSRedirector(context,
                        getNetwork().getLocalAddress(),
                        HTTP_REDIR_PORT);
        } catch (Exception e) { errorLogging(tag, e); }

        return redirector;
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
    public static Vector<com.kenshi.Network.NetworkManager.Target> getTargets() { return targets; }

    @Contract(pure = true)
    public static com.kenshi.Network.NetworkManager.Target getTarget(int index) { return targets.get(index); }

    @Contract(pure = true)
    public static com.kenshi.Network.NetworkManager.Target getCurrentTarget() { return getTarget(currentTarget); }



    public static void setLastError(String error) { lastError = error; }

    public static void setForwarding(boolean enabled) {
        Log.d(tag, "Setting ipv4 forwarding to " + enabled);

        String status = (enabled ? "1" : "0"),
                command = "echo " + status + " > " + ipv4ForwardFilePath;

        try { Shell.exec(command); }
        catch (Exception e) { errorLogging(tag, e); }
    }

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
            networkChecker = new NetworkChecker(context);

            Target target = new Target(networkChecker),

                    gateway = new Target(networkChecker.getGatewayAddress(),
                            networkChecker.getGatewayHardware()),

                    device = new Target(networkChecker.getLocalAddress(),
                            networkChecker.getLocalHardware());

            gateway.setAlias(networkChecker.getSSID());
            device.setAlias(android.os.Build.MODEL);

            targets.add(target);
            targets.add(gateway);
            targets.add(device);

            initialized = true;
        } catch(Exception e) { errorLogging(tag, e); throw e; }
    }

    public static void reloadNetworkMapping() {
        try {
            networkChecker = new NetworkChecker(context);

            Target network = new Target(networkChecker),
                    gateway = new Target(networkChecker.getGatewayAddress(),
                            networkChecker.getGatewayHardware()),

                    device = new Target(networkChecker.getLocalAddress(),
                            networkChecker.getGatewayHardware());

            gateway.setAlias(networkChecker.getSSID());
            device.setAlias(Build.MODEL);

            targets.clear();
            targets.add(network);
            targets.add(gateway);
            targets.add(device);

            initialized = true;
        } catch (Exception e) { errorLogging(tag, e); }
    }

    public static boolean checkNetworking(final Activity current) {
        if(!NetworkChecker.isWifiConnected(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(current);
            builder.setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Wifi connectivity went down")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(WifiScannerActivity.CONNECTED, false);
                            Intent intent = new Intent();
                            intent.putExtras(bundle);
                            current.setResult(Activity.RESULT_OK, intent);
                            current.finish();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return false;
        }
        return true;
    }

    public static boolean isServiceRunning(String name) {
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);

        for(ActivityManager.RunningServiceInfo serviceInfo :
                activityManager.getRunningServices(Integer.MAX_VALUE))
            if(name.equals(serviceInfo.service.getClassName()))
                return true;

        return false;
    }

    public static boolean isPortAvailable(int port) {
        boolean available = false;
        int availableCode = openPorts.get(port);

        if (availableCode != 0)
            return availableCode != 1;

        try {
            //attemp 3 times since proxy and server could be still releasing their ports
            for(int index = 0; index < 3; index++) {
                Socket channel = new Socket();
                InetSocketAddress inetSocketAddress = new InetSocketAddress(
                        InetAddress.getByName(networkChecker.getLocalNetworkAsString()),
                        port
                );

                channel.connect(inetSocketAddress, 200);
                available = !channel.isConnected();
                channel.close();
                Thread.sleep(200);
            }
        } catch (Exception e) { available = true; }

        openPorts.put(port, available ? 2 : 1);
        return available;
    }

    public static ArrayList<String> getAvailableSessionFiles() {
        ArrayList<String> files = new ArrayList<>();
        File storage = new File(storagePath);

        if(storage != null && storage.exists()) {
            String[]children = storage.list();

            if(children != null && children.length > 0)
                for(String child : children)
                    if(child.endsWith(".dss"))
                        files.add(child);
        }
        return files;
    }

    public static String saveSession(String sessionname) throws IOException {
        StringBuilder builder = new StringBuilder();
        String filename = storagePath + "/" + sessionname + ".dss",
                session = null;

        builder.append(SESSION_MAGIC + "\n");

        //skip the network target
        builder.append((targets.size() - 1) + "\n");

        for(Target target : targets)
            if(target.getType() != Target.Type.NETWORK)
                target.serialize(builder);

        builder.append(currentTarget);
        session = builder.toString();

        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);

        gzipOutputStream.write(session.getBytes());
        gzipOutputStream.close();

        sessionName = sessionname;
        return filename;
    }

    public static ArrayList<String> getAvailableHijackerSessionFiles() {
        ArrayList<String> files = new ArrayList<>();
        File storage = new File(storagePath);

        if(storage != null && storage.exists()) {
            String[]children = storage.list();

            if(children != null && children.length > 0)
                for(String child : children)
                    if(child.endsWith(".dhs"))
                        files.add(child);
        }

        return files;
    }

    public static String saveHijackerSession(String sessionname, HijackerActivity.Session session)
            throws IOException{
        StringBuilder builder = new StringBuilder();
        String filename = storagePath + "/" + sessionname + ".dhs",
                buffer = null;

        builder.append(SESSION_MAGIC + "\n");
        builder.append(session.HTTPS).append("\n");
        builder.append(session.address).append("\n");
        builder.append(session.domain).append("\n");
        builder.append(session.useragent).append("\n");
        builder.append(session.cookies.size()).append("\n");
        for(BasicClientCookie cookie : session.cookies.values())
            builder.append(
                    cookie.getName() + "=" + cookie.getValue() + "; domain=" +
                            cookie.getDomain() + "; path=/" +
                            (session.HTTPS ? ";secure" : "") + "\n"
            );
        buffer = builder.toString();

        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);

        gzipOutputStream.write(buffer.getBytes());
        gzipOutputStream.close();
        sessionName = sessionname;

        return filename;
    }

    public static void loadSession(String filename) throws Exception {
        //
    }

    public static synchronized void errorLogging(String tag, Exception e) {
        String message = "Unknown error",
                trace = "Unknown trace",
                filename = (
                        new File(Environment.getExternalStorageDirectory().toString(),
                                ERROR_LOG_FILENAME
                        )
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

    public static boolean isARM() {
        String abi = Build.CPU_ABI;
        Log.d(tag, "Build.CPU_ABI = " + abi);
        return Build.CPU_ABI.toLowerCase().startsWith("armeabi");
    }

    public static synchronized void errorLog(String tag, String data) {
        String filename = (
                new File(
                        Environment.getExternalStorageDirectory().toString(), ERROR_LOG_FILENAME
                ).getAbsolutePath()
        );
        data = data.trim();

        if(context != null &&
            getSettings().getBoolean("PREF_DEBUG_ERROR_LOGGING", false))
        {
            try {
                FileWriter fileWriter = new FileWriter(filename, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write(data);
                bufferedWriter.close();
            } catch (IOException e) { Log.e(System.tag, e.toString()); }
        }

        Log.e(tag, data);
    }

    public static synchronized void setCustomData(Object data) { customData = data; }
}
