package com.kenshi.NetworkManager;

import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.NetworkInterface;

public class NetworkChecker {
    private static final String tag = "NETWORK CHECKER";

    public enum Protocol {
        TCP,
        UDP,
        ICMP,
        IGMP,
        UNKNOWN;

        public static Protocol fromString(String proto) {
            if(!proto.isEmpty()) {
                proto = proto.toLowerCase();
                switch (proto) {
                    case "tcp":
                        return TCP;
                    case "upd":
                        return UDP;
                    case "icmp":
                        return ICMP;
                    case "igmp":
                        return IGMP;
                }
            }
            return UNKNOWN;
        }
    }

    private ConnectivityManager connectivityManager = null;
    private WifiManager wifiManager = null;
    private DhcpInfo dhcpInfo = null;
    private WifiInfo wifiInfo = null;
    private NetworkInterface networkInterface = null;
    private Ip4Address gateway = null,
            netmask = null,
            local = null,
            base = null;

}
