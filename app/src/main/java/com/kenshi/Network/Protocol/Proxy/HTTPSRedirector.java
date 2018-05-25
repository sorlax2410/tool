package com.kenshi.Network.Protocol.Proxy;

import android.content.Context;

import com.kenshi.Core.System;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLServerSocket;

public class HTTPSRedirector implements Runnable {
    private static final String tag = "HTTPS.REDIRECT";
    private static final String KEYSTORE_FILE = "mitdroid.keystore";
    private static final String KEYSTORE_PASS = "mitdroid";
    private static final int BACKLOG = 255;

    private Context context = null;
    private InetAddress inetAddress = null;
    private int port = System.HTTP_REDIR_PORT;
    private boolean running = false;
    private SSLServerSocket sslServerSocket = null;

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

    }

    public HTTPSRedirector(Context context, InetAddress inetAddress, int port)
        throws UnknownHostException,
            IOException,
            KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException,
            UnrecoverableKeyException,
            KeyManagementException
    {
        this.context = context;
        this.inetAddress = inetAddress;
        this.port = port;
    }
}
