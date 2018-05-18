package com.kenshi.tools;

import android.content.Context;

public class IPTables extends Extender {
    public IPTables(String name, Context context) {
        super(name, context);
    }

    public IPTables(String name) {
        super(name);
    }

    public IPTables() {
        super("iptables");
    }
}
