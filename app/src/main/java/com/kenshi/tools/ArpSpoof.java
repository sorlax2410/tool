package com.kenshi.tools;

import android.content.Context;

public class ArpSpoof extends Extender {
    public ArpSpoof(String name, Context context) {
        super(name, context);
    }

    public ArpSpoof(String name) {
        super(name);
    }

    public ArpSpoof(Context context) {
        super("arpspoof/arpspoof", context);
    }
}
