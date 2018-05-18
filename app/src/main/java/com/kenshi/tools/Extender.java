package com.kenshi.tools;

import android.content.Context;

import com.kenshi.Core.Shell;
import com.kenshi.Core.System;

import java.io.File;
import java.io.IOException;

public class Extender {
    private static final String tag = "EXTENDER";

    protected File file = null;
    protected String name = null;
    protected String directoryName = null;
    protected String filename = null;
    protected String libPath = null;
    protected Context context = null;

    public Extender(String name, Context context) {
        this.context = context;
        libPath = this.context.getFilesDir().getAbsolutePath() + "/tools/libs";
        filename = this.context.getFilesDir().getAbsolutePath() + "/tools/" + name;
        file = new File(filename);
        this.name = file.getName();
        directoryName = file.getParent();
    }

    public Extender(String name) { this.name = name; }

    public void run(String args, Shell.OutputReceiver receiver)
        throws IOException, InterruptedException
    {
        String command = null;

        if(context != null)
            command = "cd " + directoryName + "&& ./" + name + " " + args;
        else
            command = name + " " + args;

        Shell.exec(command, receiver);
    }

    public void run(String args) throws IOException, InterruptedException { run(args, null); }

    public Thread async(String args, Shell.OutputReceiver receiver) {
        String command = null;

        if(context != null)
            command = "cd " + directoryName + " && ./" + name + " " + args;
        else
            command = name + " " + args;

        return Shell.async(command, receiver);
    }

    public Thread asyncStatic(String args, Shell.OutputReceiver receiver) {
        String command = null;

        if(context != null)
            command = "cd " + directoryName + " && ./" + name + " " + args;
        else
            command = name + " " + args;

        return Shell.async(command, receiver, false);
    }

    public boolean kill(String signal) {
        try {
            Shell.exec("killall -" + signal + " " + name);
            return true;
        } catch (Exception e) { System.errorLogging(tag, e); }
        return false;
    }

    public boolean kill() { return kill("9"); }
}
