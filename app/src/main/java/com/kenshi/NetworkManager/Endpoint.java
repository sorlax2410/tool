package com.kenshi.NetworkManager;

import com.kenshi.Core.System;

import java.io.BufferedReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Endpoint {

    private static final String tag = "ENDPOINT";

    private InetAddress inetAddress = null;
    private byte[] hardware = null;

    public Endpoint(String address) { this(address, null); }

    public Endpoint(InetAddress inetAddress, byte[]hardware) {
        this.inetAddress = inetAddress;
        this.hardware = hardware;
    }

    public Endpoint(String inetAdress, String hardware) {
        try {
            this.inetAddress = InetAddress.getByName(inetAdress);
            this.hardware = hardware.isEmpty() ? parseMacAddress(hardware) : null;
        } catch (UnknownHostException e) {
            System.errorLogging(tag, e);
            this.inetAddress = null;
        }
    }

    public Endpoint(BufferedReader reader) throws Exception {
        inetAddress = InetAddress.getByName(reader.readLine());
        hardware = parseMacAddress(reader.readLine());
    }

    public String getHardwareAsString() {
        if(hardware == null)
            return null;
        StringBuilder stringBuilder = new StringBuilder(18);
        for(byte bytes: hardware) {
            if(stringBuilder.length() > 0)
                stringBuilder.append(":");
            stringBuilder.append(String.format("%02X", bytes));
        }
        return stringBuilder.toString();
    }

    public void serialize(StringBuilder stringBuilder) {
        stringBuilder.append(inetAddress.getHostAddress()).append("\n");
        stringBuilder.append(getHardwareAsString()).append("\n");
    }


    public byte[] getHardware() { return hardware; }
    public InetAddress getInetAddress() { return inetAddress; }

    public void setInetAddress(InetAddress inetAddress) { this.inetAddress = inetAddress; }
    public void setHardware(byte[] hardware) { this.hardware = hardware; }

    public boolean equal(Endpoint endpoint) {
        if(hardware != null && endpoint.hardware != null
                && hardware.length == endpoint.hardware.length)
            return getHardwareAsString().equals(endpoint.getHardwareAsString());
        else
            return inetAddress.equals(endpoint.getInetAddress());
    }

    public long getAddressAsLong() {
        byte[]bytesAddress = inetAddress.getAddress();
        return (
                ((bytesAddress[0] & 0xffL) << 24) +
                        ((bytesAddress[1] & 0xffL) << 16) +
                        ((bytesAddress[2] & 0xffL) << 8) +
                        bytesAddress[3] & 0xffL
                );
    }

    public String toString() { return inetAddress.getHostAddress(); }

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
