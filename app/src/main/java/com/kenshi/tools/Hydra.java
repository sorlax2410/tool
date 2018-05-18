package com.kenshi.tools;

import android.content.Context;

public class Hydra extends Extender {
    public Hydra(String name, Context context) {
        super(name, context);
    }

    public Hydra(String name) {
        super(name);
    }

    public Hydra(Context context) {
        super("hydra/hydra" ,context);
    }
}
