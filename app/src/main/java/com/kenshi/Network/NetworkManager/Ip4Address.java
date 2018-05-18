package com.kenshi.Network.NetworkManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class Ip4Address {
    private byte[] byteArray = null;
    private String hostAddress = null;
    private int integer = 0;
    private InetAddress inetAddress = null;

    public Ip4Address(int address) throws UnknownHostException {
        byteArray = new byte[4];
        integer = address;

        if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            byteArray[0] = (byte)(address & 0xff);
            byteArray[1] = (byte)(0xff & address >> 8);
            byteArray[2] = (byte)(0xff & address >> 16);
            byteArray[3] = (byte)(0xff & address >> 24);
        }
        else {
            byteArray[0] = (byte)(0xff & address >> 24);
            byteArray[1] = (byte)(0xff & address >> 16);
            byteArray[2] = (byte)(0xff & address >> 8);
            byteArray[3] = (byte)(address & 0xff);
        }
        inetAddress = InetAddress.getByAddress(byteArray);
        hostAddress = inetAddress.getHostAddress();
    }

    public Ip4Address(String address) throws UnknownHostException {
        hostAddress = address;
        inetAddress = InetAddress.getByName(address);
        byteArray = inetAddress.getAddress();
        integer = ((byteArray[0] & 0xff) << 24) +
                ((byteArray[1] & 0xff) << 16) +
                ((byteArray[2] & 0xff) << 8) +
                (byteArray[3] & 0xff);
    }

    public Ip4Address(byte[]address) throws UnknownHostException {
        byteArray = address;
        inetAddress = InetAddress.getByAddress(byteArray);
        hostAddress = inetAddress.getHostAddress();
        integer = ((byteArray[0] & 0xff) << 24) +
                ((byteArray[1] & 0xff) << 16) +
                ((byteArray[2] & 0xff) << 8) +
                (byteArray[3] & 0xff);
    }

    public Ip4Address(InetAddress inetAddress) throws UnknownHostException {
        this.inetAddress = inetAddress;
        byteArray = inetAddress.getAddress();
        hostAddress = inetAddress.getHostAddress();
        integer = ((byteArray[0] & 0xff) << 24) +
                ((byteArray[1] & 0xff) << 16) +
                ((byteArray[2] & 0xff) << 8) +
                (byteArray[3] & 0xff);
    }

    public static int ntohl(int number) {
        return (
                ((number >> 24) & 0xff) +
                        ((number >> 16) & 0xff) +
                        ((number >> 8) & 0xff) +
                        number&0xff
                );
    }

    public static Ip4Address next(Ip4Address address) throws UnknownHostException {
        byte[]Address = address.byteArray;
        int index = Address.length - 1;

        while(index >= 0 && Address[index] == (byte)0xff) {
            Address[index] = 0;
            index--;
        }
        if(index >= 0) {
            Address[index]++;
            return new Ip4Address(Address);
        }
        else
            return null;
    }


    public String getHostAddress() { return hostAddress; }
    public int getInteger() { return integer; }
    public InetAddress getInetAddress() { return inetAddress; }
    public byte[] getByteArray() { return byteArray; }

    public boolean equals(Ip4Address address) { return integer == address.getInteger(); }
    public boolean equals(InetAddress address) { return inetAddress.equals(address); }

    public int getPrefixLength() {
        int bits, index, number = integer;
        for(index = 0, bits = (number & 1); index < 32; index++, number >>>= 1, bits += number + 1);
        return bits;
    }
}
