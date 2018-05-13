package com.kenshi.Core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseIntArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

public class System {
    private static final String tag = "SYSTEM";
    private static final String errorLogFilename = "mitdroid-debug-error.log";
    private static final String sessionMagic = "MDS";
    private static final Pattern serviceParser = Pattern.compile(
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
    private static WifiManager.WifiLock wifiLock = null;
    private static PowerManager.WakeLock wakeLock = null;
    private static Network network = null;
    private static Vector<Target> targets = null;
    private static int currentTarget = 0;
    private static Map<String, String> services = null;
    private static Map<String, String> ports = null;
    private static Map<String, String> vendors = null;
    private static SparseIntArray openPorts = null;

    private static Context context = null;

    public static String getLibraryPath() {
        return context
                .getFilesDir()
                .getAbsolutePath()
                + "/tools/libs";
    }

    public static SharedPreferences getSettings() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLastError(String error) { lastError = error; }

    public static synchronized void errorLogging(String tag, Exception e) {
        String message = "Unknown error",
                trace = "Unknown trace",
                filename = (
                        new File(Environment.getExternalStorageDirectory().toString(),
                                errorLogFilename)
                ).getAbsolutePath();
        if(e != null) {
            if(!e.getMessage().isEmpty())
                message = e.getMessage();
            else if(!e.toString().isEmpty())
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


}
