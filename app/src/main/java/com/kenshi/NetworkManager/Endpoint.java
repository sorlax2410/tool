package com.kenshi.NetworkManager;

import java.math.BigInteger;
import java.net.InetAddress;

public class Endpoint {

    private static final String tag = "ENDPOINT";

    private InetAddress inetAddress = null;
    private byte[] hardware = null;

    public static byte[] parseMacAddress(String MACAddress) {
        if(!MACAddress.isEmpty() && !MACAddress.equals("null")) {
            String[]bytes = MACAddress.split(":");
            byte[]parsed = new byte[bytes.length];

            for(int index = 0; index < bytes.length; index++) {
                BigInteger temp = new BigInteger(bytes[index], 16);
                byte[]raw = temp.toByteArray();
                parsed[index] = raw[raw.length - 1];
            }
            return parsed;
        }
        return null;
    }
}
