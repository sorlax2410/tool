package com.kenshi.Network.Protocol.Proxy;

import android.util.Log;

import com.kenshi.Core.System;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Proxy implements Runnable {

    public static interface OnRequestListener {
        public void onRequest(boolean isHttps, String address, String hostname,
                              ArrayList<String>headers);
    }

    public static interface ProxyFilter { public String onDataReceived(String header, String data); }

    private static final String tag = "PROTOCOL.PROXY";
    private static final int BACKLOG = 255;

    private InetAddress inetAddress = null;
    private int port = System.HTTP_PROXY_PORT;
    private boolean running = false;
    private ServerSocket socket = null;
    private OnRequestListener requestListener = null;
    private ArrayList<ProxyFilter>filters = null;
    private String hostRedirect = null;
    private int portRedirect = 80;

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            if(socket == null)
                socket = new ServerSocket(port, BACKLOG, inetAddress);

            Log.d(tag, "Proxy started on " + inetAddress + ":" + port);

            running = true;

            while(running && socket != null) {
                try {

                } catch (Exception e) { System.errorLogging(tag, e); }
            }
        } catch (IOException e) { System.errorLogging(tag, e); }
    }

    public Proxy(InetAddress inetAddress, int port) throws UnknownHostException, IOException {
        this.inetAddress = inetAddress;
        this.port = port;
        socket = new ServerSocket(this.port, BACKLOG, this.inetAddress);
        filters = new ArrayList<>();
    }

    public Proxy(String address, int port) throws UnknownHostException, IOException {
        this(InetAddress.getByName(address), port);
    }

    public void setOnRequestListener(OnRequestListener listener) { this.requestListener = listener; }

    public void setRedirection(String host, int port) {
        this.hostRedirect = host;
        this.portRedirect = port;
    }

    public void setFilter(ProxyFilter filters) {
        this.filters.clear();
        if(filters != null)
            this.filters.add(filters);
    }

    public void addFilter(ProxyFilter filter) { this.filters.add(filter); }

    public void stop() {
        Log.d(tag, "Stopping proxy . . .");

        try {
            if(socket != null)
                socket.close();
        } catch (IOException ignored) {}

        running = false;
        socket = null;
    }
}
