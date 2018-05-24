package com.kenshi.tools;

import android.content.Context;

import com.kenshi.Core.Shell;

import java.util.regex.Pattern;

public class Ettercap extends Extender {
    public Ettercap(String name, Context context) {
        super(name, context);
    }

    public Ettercap(String name) {
        super(name);
    }

    public Ettercap(Context context) {
        super("ettercap/ettercap" ,context);
    }

    public static abstract class OnAccountListener implements Shell.OutputReceiver {
        private static final Pattern ACCOUNT_PATTERN = Pattern.compile("^([^\\s]+)\\s+:\\s+([^\\:]+):(\\d+).+",
                Pattern.CASE_INSENSITIVE);
        @Override
        public void onStart(String command) {}

        @Override
        public void onNewLine(String line) {

        }

        @Override
        public void onEnd(int exitCode) {

        }

        public abstract void onAccount(String protocol, String address, String port, String line);
    }
}
