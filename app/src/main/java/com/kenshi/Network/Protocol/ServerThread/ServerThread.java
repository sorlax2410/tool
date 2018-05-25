package com.kenshi.Network.Protocol.ServerThread;

import android.util.Log;

import com.kenshi.Core.System;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ServerThread extends Thread {

    private final static String tag = "SERVERTHREAD";
    private final static int MAX_REQUEST_SIZE = 8192;

    private Socket socket = null;
    private BufferedOutputStream bufferedOutputStream = null;
    private InputStream reader = null;
    private byte[]data = null;
    private String contentType = null;

    public ServerThread(Socket socket, byte[]data, String contentType) throws IOException {
        super("Server Thread");

        this.socket = socket;
        this.data = data;
        this.contentType = contentType;
        bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
        reader = socket.getInputStream();
    }


    /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see #start()
     * @see #stop()
     * //@see #Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
        try {
            //Apache's default header limit is 8KB
            byte[]request = new byte[MAX_REQUEST_SIZE];

            //Read the request
            if(reader.read(request, 0, MAX_REQUEST_SIZE) > 0) {
                String header = "HTTP/1.1 200 OK\r\n" +
                        "Content-type: " + contentType + "\r\n" +
                        "Content-length: " + data.length + "\r\n\n";
                bufferedOutputStream.write(header.getBytes());
                bufferedOutputStream.write(data);
            }
            else
                Log.w(tag, "Empty HTTP Request.");
        } catch (IOException e) { System.errorLogging(tag, e); }
        finally {
            try {
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                reader.close();
            } catch (IOException e) { System.errorLogging(tag, e); }
        }
    }
}
