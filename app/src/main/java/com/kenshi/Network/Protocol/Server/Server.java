package com.kenshi.Network.Protocol.Server;

import android.util.Log;

import com.kenshi.Core.System;
import com.kenshi.Network.Protocol.ServerThread.ServerThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server implements Runnable {
    private static final String tag = "PROTOCOL.SERVER";
    private static final int BACKLOG = 255;
    private static final int MAX_FILE_SIZE = 10*1024*1024;

    private InetAddress inetAddress = null;
    private int port = System.HTTP_SERVER_PORT;
    private boolean running = false;
    private ServerSocket serverSocket = null;
    private String resourcePath = null,
            resourceContentType = null;
    private byte[]resourceData = null;

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
            if(serverSocket == null)
                serverSocket = new ServerSocket(port, BACKLOG, inetAddress);
            Log.d(tag, "Server started on " + inetAddress + ":" + port);
            running = true;
            while(running) {
                try {
                    Socket client = serverSocket.accept();
                    new ServerThread(client, resourceData, resourceContentType).start();
                } catch (IOException e) { System.errorLogging(tag, e); }
            }
            Log.d(tag, "Server stopped.");
        } catch (IOException e) { System.errorLogging(tag, e); }
    }

    public void stop() {
        Log.d(tag, "Stopping server . . .");

        try {
            if(serverSocket != null)
                serverSocket.close();
        } catch (IOException ignored) {}

        running = false;
        serverSocket = null;
    }

    public void setResource(String resourcePath, String resourceContentType) throws IOException {
        this.resourceContentType =resourceContentType;
        this.resourcePath = resourcePath;

        //preload resource data
        File file = new File(this.resourcePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        long size = file.length();
        int offset = 0,
                read = 0;

        if(size > MAX_FILE_SIZE)
            throw new IOException("Max file allowed size is " + MAX_FILE_SIZE + " bytes");

        resourceData = new byte[(int) size];

        while(offset < size &&
                (read = fileInputStream.read(resourceData, offset, (int)(size - offset))) > -1)
            offset += read;

        if(offset < size)
            throw new IOException("Could not completely read file " + file.getName() + " .");

        fileInputStream.close();
    }

    public Server(InetAddress inetAddress, int port, String resourcePath, String resourceContentType)
        throws UnknownHostException, IOException
    {
        this.inetAddress = inetAddress;
        this.port = port;
        serverSocket = new ServerSocket(this.port, BACKLOG, this.inetAddress);

        if(resourceContentType != null && resourcePath != null)
            setResource(resourcePath, resourceContentType);
    }

    public Server(String address, int port) throws UnknownHostException, IOException {
        this(address, port, null, null);
    }

    public Server(InetAddress inetAddress, int port) throws UnknownHostException, IOException {
        this(inetAddress, port, null, null);
    }

    public Server(String address, int port, String resourceContentType, String resourcePath)
            throws UnknownHostException, IOException
    {
        this(InetAddress.getByName(address), port, resourcePath, resourceContentType);
    }

    public String getResourceURL() {
        return "http://" + inetAddress.getHostAddress() +
                ":" + port + "/";
    }
}
