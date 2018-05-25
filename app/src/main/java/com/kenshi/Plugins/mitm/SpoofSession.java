package com.kenshi.Plugins.mitm;

import com.kenshi.Core.System;
import com.kenshi.Network.NetworkManager.Target;
import com.kenshi.tools.Ettercap;

public class SpoofSession {

    public static abstract interface OnSessionReadyListener {
        void onSessionReady();
        void onError(String error);
    }

    private static final String tag = "SPOOFSESSION";

    private boolean isWithProxy = false,
            isWithServer = false;
    private String serverFilename = null,
            serverMimeType = null;

    public static abstract interface onSessionReadyListener {
        void onSessionReady();
        void onError(String error);
    }

    public SpoofSession(boolean isWithProxy, boolean isWithServer, String serverFilename,
                        String serverMimeType)
    {
        this.isWithProxy = isWithProxy;
        this.isWithServer = isWithServer;
        this.serverFilename = serverFilename;
        this.serverMimeType = serverMimeType;
    }

    public SpoofSession() {
        //standard spoof session with only transparrent proxy
        this(true, false, null, null);
    }

    public void stop() {
        return;
    }

    public void start(Target target, final onSessionReadyListener listener) {
        this.stop();

        if(isWithProxy) {
            if(System.getProxy() == null) {
                listener.onError("Unable to create proxy," +
                        " please check your connection and port availability");
                return;
            }

            if(System.getSettings().getBoolean("PREF_HTTPS_REDIRECT", true)) {
                if(System.getHttpsRedirector() == null) {
                    listener.onError("Unable to create HTTPS redirector," +
                            " please check your connection and port availability");
                    return;
                }
                new Thread(System.getHttpsRedirector()).start();
            }

            new Thread(System.getProxy()).start();
        }

        if(isWithServer) {
            try {
                if(System.getServer() == null) {
                    listener.onError("Unable to create resource server," +
                            " please check your connection and port availability");
                    return;
                }

                System.getServer().setResource(serverFilename, serverMimeType);
                new Thread(System.getServer()).start();
            } catch (Exception e) { System.errorLogging(tag, e); isWithServer = false; }
        }
    }

    public void start(final Target target ,final Ettercap.OnAccountListener onAccountListener) {

    }

    public void start(Ettercap.OnAccountListener onAccountListener) {

    }

    public void start(final OnSessionReadyListener listener) {

    }
}
