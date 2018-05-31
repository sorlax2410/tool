package com.kenshi.Network.Protocol.ProxyThread;

import android.util.Log;

import com.kenshi.Core.Profiler;
import com.kenshi.Core.System;
import com.kenshi.Network.Protocol.Proxy.Proxy;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

public class ProxyThread extends Thread {
    private final static String tag = "PROXYTHREAD";
    private final static int MAX_REQUEST_SIZE = 8192,
            HTTP_SERVER_PORT = 80,
            HTTPS_SERVER_PORT = 443;
    private final static Pattern LINK_PATTER = Pattern.compile(
            "(https://[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
            Pattern.CASE_INSENSITIVE
    );

    private Socket socket = null;
    private BufferedOutputStream writer = null;
    private InputStream reader = null;
    private String serverName = null;
    private Socket server = null;
    private InputStream serverReader = null;
    private OutputStream serverWriter = null;
    private Proxy.OnRequestListener requestListener = null;
    private ArrayList<Proxy.ProxyFilter> filters = null;
    private String hostRedirect = null;
    private int portRedirect = 80;
    private SocketFactory socketFactory = null;

    public ProxyThread(Socket socket, Proxy.OnRequestListener listener,
                       ArrayList<Proxy.ProxyFilter> filters, String hostRedirect, int portRedirect)
        throws IOException
    {
        super("ProxyThread");

        this.socket = socket;
        this.writer = null;
        this.reader = this.socket.getInputStream();
        this.requestListener = listener;
        this.filters = filters;
        this.hostRedirect = hostRedirect;
        this.portRedirect = portRedirect;
        this.socketFactory = SSLSocketFactory.getDefault();
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
     * @see #Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
        try {
            //Apache's default header limit is 8KB
            byte[]buffer = new byte[MAX_REQUEST_SIZE];
            int read = 0;

            final String client = this.socket.getInetAddress().getHostAddress();

            Log.d(tag, "Connection from " + client);

            Profiler.instance().profile("proxy request read");

            //read the header and rebuild it
            if((read = this.reader.read(buffer, 0, MAX_REQUEST_SIZE)) > 0) {
                Profiler.instance().profile("proxy request parse");

                ByteArrayInputStream byteArrayInputStream = new
                        ByteArrayInputStream(buffer, 0, read);
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(byteArrayInputStream)
                );
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                ArrayList<String>headers = new ArrayList<>();
                boolean headerProcessed = false;

                while((line = bufferedReader.readLine()) != null) {
                    if(!headerProcessed) {
                        headers.add(line);
                        // \r\n\r\n received?
                        if(line.trim().isEmpty())
                            headerProcessed = true;
                        //set protocol version to 1.0 since we don't support chunked transfer encoding
                        else if(line.contains("HTTP/1.1"))
                            line = line.replace("HTTP/1.1", "HTTP/1.0");
                        //Fix headers
                        else if(line.indexOf(':') != -1) {
                            String[]split = line.split(":", 2);
                            String header = split[0].trim(),
                                    value = split[1].trim();
                            //set encoding to identity since we are not handling gzipped streams
                            //if(header.equals())
                        }
                    }
                }
            }
        } catch (IOException e) { System.errorLogging(tag, e); }
    }
}
