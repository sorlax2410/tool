package com.kenshi.NetworkManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.kenshi.Core.System;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Objects;

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
            localHost = null,
            base = null;

    public boolean isConnected() {
        return connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected();
    }

    public InetAddress getLocalAddress() { return localHost.getInetAddress(); }

    /**
     * @Issue#26: Initialization error in ColdFusionX ROM
     *
     * @Description: It seems it's a ROM issue which doesn't correctly populate device descriptors.
     * This rom maps the default wifi interface to a generic usb device
     * ( maybe it's missing the specific interface driver ), which is obviously not, and
     * it all goes shit, use an alternative method to obtain the interface object.
     **/
    public NetworkChecker(Context context)
            throws NoRouteToHostException, SocketException, UnknownHostException
    {
        wifiManager = (WifiManager)context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager)context
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        dhcpInfo = wifiManager.getDhcpInfo();
        wifiInfo = wifiManager.getConnectionInfo();
        gateway = new Ip4Address(dhcpInfo.gateway);
        netmask = new Ip4Address(dhcpInfo.netmask);
        localHost = new Ip4Address(dhcpInfo.ipAddress);
        base = new Ip4Address(dhcpInfo.netmask & dhcpInfo.gateway);

        if(!isConnected())
            throw new NoRouteToHostException("Not connected to any wifi access point");
        else {
            try {
                networkInterface = NetworkInterface.getByInetAddress(getLocalAddress());
                if(networkInterface == null)
                    throw new IllegalStateException("Error retrieving network interface");
            } catch (SocketException e) {
                System.errorLogging(tag, e);
                networkInterface = NetworkInterface.getByName(
                        java.lang.System.getProperty(
                                "wifi.interface",
                                "wlan0"
                        )
                );

                if(networkInterface == null)
                    throw e;
            }
        }
    }

    public DhcpInfo getDhcpInfo() { return dhcpInfo; }
    public boolean equals(NetworkChecker networkChecker) {
        return dhcpInfo.equals(networkChecker.getDhcpInfo());
    }

    public boolean isInternal(String ip) {
        try {
            byte[]gateway = this.gateway.getByteArray();
            byte[]address = InetAddress.getByName(ip).getAddress();
            byte[]mask = this.netmask.getByteArray();
            for(int index = 0; index < gateway.length; index++)
                if((gateway[index] & mask[index]) != (address[index] & mask[index]))
                    return false;
            return true;
        } catch (UnknownHostException e) { System.errorLogging(tag, e); }
        return false;
    }

    public boolean isInternal(int ip) {
        return isInternal(
                (ip & 0xff) +
                        "." + ((ip >> 8) & 0xff) +
                        "." + ((ip >> 16) & 0xff) +
                        "." + ((ip >> 24) & 0xff)
        );
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects
                .requireNonNull(connectivityManager)
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected() && networkInfo.isAvailable();
    }

    public static boolean isConnectivityAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = Objects
                .requireNonNull(connectivityManager)
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    public String getSSID() { return wifiInfo.getSSID(); }

    public int getNumberOfAddresses() { return Ip4Address.ntohl(~netmask.getInteger()); }

    public Ip4Address getStartAddress() { return this.base; }

    public String getNetworkMask() {
        int network = base.getInteger();
        return (
                (network & 0xff) + "." +
                        ((network >> 8) & 0xff) + "." +
                        ((network >> 16) & 0xff) + "." +
                        ((network >> 24) & 0xff) + "."
                );
    }

    public String networkRepresentation() {
        return getNetworkMask() + "/" + netmask.getPrefixLength();
    }

    public InetAddress getNetmaskAddress() { return netmask.getInetAddress(); }
    public InetAddress getGatewayAddress() { return gateway.getInetAddress(); }

    public byte[] getGatewayHardware() { return Endpoint.parseMacAddress(wifiInfo.getBSSID()); }

}
