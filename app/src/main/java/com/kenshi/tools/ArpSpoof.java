package com.kenshi.tools;

import android.content.Context;

import com.kenshi.Core.Shell;
import com.kenshi.Core.System;
import com.kenshi.Network.NetworkManager.Target;

public class ArpSpoof extends Extender {
    private static final String tag = "ARPSPOOF";

    public ArpSpoof(String name, Context context) {
        super(name, context);
    }

    public ArpSpoof(String name) {
        super(name);
    }

    public ArpSpoof(Context context) {
        super("arpspoof/arpspoof", context);
    }

    public Thread spoof(Target target, Shell.OutputReceiver receiver) {
        String commandLine = "";

        try {
            if(target.getType() == Target.Type.NETWORK)
                commandLine = "-i " + System.getNetwork().getNetworkInterface().getDisplayName() +
                        " " + System.getGatewayAddress();
            else
                commandLine = "-i " + System.getNetwork().getNetworkInterface().getDisplayName() +
                        " -t " +  target.getCommandLineRepresentation();
        } catch (Exception e) { System.errorLogging(tag, e); }

        return super.asyncStatic(commandLine, receiver);
    }

    /**
     * @Description: arpspoof needs SIGINT (ctrl + c) to restore arp table
     * @return: successfully killed the process
     */
    public boolean kill() { return super.kill("SIGINT"); }
}
