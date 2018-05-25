package com.kenshi.Network.Protocol.Proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Proxy implements Runnable {
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

    public Proxy(InetAddress inetAddress, int port) throws UnknownHostException, IOException {

    }
}
