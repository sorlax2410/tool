package com.kenshi.tools;

import android.content.Context;

public class TcpDump extends Extender {
    public TcpDump(String name, Context context) {
        super(name, context);
    }

    public TcpDump(String name) {
        super(name);
    }

    public TcpDump(Context context) {
        super("tcpdump/tcpdump" ,context);
    }
}
