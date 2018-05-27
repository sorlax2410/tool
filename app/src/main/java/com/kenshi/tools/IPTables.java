package com.kenshi.tools;

import android.content.Context;
import android.util.Log;

import com.kenshi.Core.System;

public class IPTables extends Extender {
    private static final String tag = "IPTABLES";

    public IPTables(String name) {
        super(name);
    }

    public IPTables(String name, Context context) {
        super(name, context);
    }

    public IPTables() {
        super("iptables");
    }

    public void portRedirect(int from, int to) {
        Log.d(tag, "Redirecting traffic from port " + from + " to port " + to);

        try {
            //clear nat
            super.run("-t nat -F");
            //clear
            super.run("-F");
            //post route
            super.run("-t nat -I POSTROUTING -s 0/0 -j MASQUERADE");
            //accept all
            super.run("-P FORWARDING ACCEPT");
            //add rule
            super.run("-t nat -A PREROUTING -j DNAT -p tcp --dport " +
                    from + " --to " + System.getNetwork().getLocalNetworkAsString() +
                    ":" + to
            );
        } catch (Exception e) { System.errorLogging(tag, e); }
    }

    public void undoPortRedirect(int from, int to) {
        Log.d(tag, "Undoing port redirection");

        try {
            //clear nat
            super.run("-t nat -F");
            //clear
            super.run("-F");
            //remove post route
            super.run("-t nat -D POSTROUTING -s 0/0 -j MASQUERADE");
            //remove rule
            super.run("-t nat -D PREROUTING -j DNAT -p tcp --dport " + from +
                    " --to " + System.getNetwork().getLocalNetworkAsString() + ":" + to
            );
        } catch (Exception e) { System.errorLogging(tag, e); }
    }

    public void trafficRedirect(String to) {
        Log.d(tag, "Redirecting traffic to " + to);
        try { super.run("-t nat -A PREROUTING -j DNAT -p tcp --to " + to); }
        catch (Exception e) { System.errorLogging(tag, e); }
    }

    public void undoTrafficRedirect(String to) {
        Log.d(tag, "Undoing traffic redirection");
        try { super.run("-t nat -D PREROUTING -j DNAT -p tcp --to " + to); }
        catch (Exception e) { System.errorLogging(tag, e); }
    }
}
