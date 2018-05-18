package com.kenshi.tools;

import android.content.Context;

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
}
