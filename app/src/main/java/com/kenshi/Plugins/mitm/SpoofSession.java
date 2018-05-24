package com.kenshi.Plugins.mitm;

import com.kenshi.Network.NetworkManager.Target;

public class SpoofSession {
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
    }
}
