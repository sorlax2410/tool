package com.kenshi.Core.Threads;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kenshi.Core.System;

public class UpdateChecker extends Thread {
    private static final String tag = "UPDATE CHECKER";
    private static final String UPDATE_CHECKING = "UpdateChecker.action.CHECKING";
    private static final String UPDATE_AVAILABLE = "UpdateChecker.action.UPDATE_AVAILABLE";
    private static final String UPDATE_NOT_AVAILABLE = "UpdateChecker.action.UPDATE_NOT_AVAILABLE";
    private static final String AVAILABLE_VERSION = "UpdateChecker.action.AVAILABLE_VERSION";

    private Context context = null;

    public UpdateChecker(Context context) {
        super("UpdateChecker");
        this.context = context;
    }

    private void send(String message, String extra, String value) {
        Intent intent = new Intent(message);

        if(!extra.isEmpty() && extra != null && value != null && !value.isEmpty())
            intent.putExtra(extra, value);
        context.sendBroadcast(intent);
    }

    public void run() {
        send(UPDATE_CHECKING, null, null);
        Log.d(tag, "SERVICE STARTED.");

        if(System.getUpdateManager().isUpdateAvailable())
            send(UPDATE_AVAILABLE, AVAILABLE_VERSION, System.getUpdateManager().getRemoteVersion());
        else
            send(UPDATE_NOT_AVAILABLE, null, null);

        Log.d(tag, "SERVICE STOPPED.");
    }
}
