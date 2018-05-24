package com.kenshi.GUI.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class FatalDialog extends AlertDialog {
    public FatalDialog(String title, String message, boolean html, final Activity activity) {
        super(activity);
        this.setTitle(title);

        if(!html)
            this.setMessage(message);
        else {
            TextView textView = new TextView(activity);

            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(Html.fromHtml(message));
            textView.setPadding(10, 10, 10, 10);

            this.setView(textView);
        }

        this.setCancelable(false);
        this.setButton(android.R.string.ok, "ok", new DialogInterface.OnClickListener() {

            /**
             * This method will be invoked when a button in the dialog is clicked.
             *
             * @param dialog the dialog that received the click
             * @param which  the button that was clicked (ex.
             *               {@link DialogInterface#BUTTON_POSITIVE}) or the position
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                java.lang.System.exit(0xff);
            }
        });
    }

    public FatalDialog(String title, String message, final Activity activity) {
        this(title, message, false, activity);
    }
}
