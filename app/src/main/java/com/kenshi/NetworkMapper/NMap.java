package com.kenshi.NetworkMapper;

import android.content.Context;
import android.util.Log;

import com.kenshi.Core.Shell;
import com.kenshi.tools.Extender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NMap extends Extender {
    public static final String tag = "NMAP";

    public NMap(Context context) { super("nmap/nmap", context); }

    public static abstract class TraceOutputReceiver implements Shell.OutputReceiver {
        private static final Pattern HOP_PATTERN = Pattern.compile(
                "^(\\d+)\\s+(\\.\\.\\.|[0-9\\.]+\\s[ms]+)\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}|[\\d]+).*",
                Pattern.CASE_INSENSITIVE
        );

        public void onStart(String commandLine) {}

        public abstract void onHop(String hop, String time, String address);

        public void onNewLine(String line) {
            Matcher matcher;

            if((matcher = HOP_PATTERN.matcher(line)) != null && matcher.find())
                onHop(matcher.group(1), matcher.group(2), matcher.group(3));
        }

        public void onEnd(int exitCode) {
            if(exitCode != 0)
                Log.e(tag, "nmap exited with code " + String.valueOf(exitCode));
        }
    }

    public Thread Trace(com.kenshi.NetworkManager.Target target, TraceOutputReceiver receiver) {
        return super.async(
                "-sn --traceroute --priviledge --send-ip --system-dns" +
                        target.getCommandLineRepresentation(),
                receiver
        );
    }
}
