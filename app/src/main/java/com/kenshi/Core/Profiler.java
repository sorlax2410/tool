package com.kenshi.Core;

import android.util.Log;

public class Profiler {
    private static volatile Profiler instance = null;

    public static Profiler instance() {
        if(instance == null)
            instance = new Profiler();
        return instance;
    }

    private volatile boolean enabled = false;
    private volatile long tick = 0;
    private volatile String profiling = null;

    public Profiler() {
        enabled = System.getSettings().getBoolean("PREF_ENABLED_PROFILER", false);
    }

    public void emit() {
        if(enabled && tick > 0 && profiling != null) {
            long delta = java.lang.System.currentTimeMillis() - tick;

            Log.d("PROFILER", "[" + profiling + "]" + format(delta));

            profiling = null;
            tick = 0;
        }
    }

    public void profile(String label) {
        emit();
        if(enabled) {
            tick = java.lang.System.currentTimeMillis();
            profiling = label;
        }
    }

    private String format(long delta) {
        if(delta < 1000)
            return delta + " ms";
        else if(delta < 60000)
            return (delta / 1000.0) + " ms";
        else
            return (delta / 60000.0) + " ms";
    }
}
