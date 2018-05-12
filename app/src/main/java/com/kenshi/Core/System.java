package com.kenshi.Core;

import android.content.Context;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.SparseIntArray;

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

    private Context context = null;
}
