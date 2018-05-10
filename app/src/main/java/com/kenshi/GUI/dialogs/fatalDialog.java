package com.kenshi.GUI.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

public class fatalDialog extends AlertDialog {
    protected fatalDialog(@NonNull Context context, @NonNull String title, @NonNull String message) {
        super(context);
    }
}
