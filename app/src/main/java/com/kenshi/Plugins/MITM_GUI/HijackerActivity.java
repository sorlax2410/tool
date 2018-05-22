package com.kenshi.Plugins.MITM_GUI;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import com.kenshi.Plugins.mitm.SpoofSession;
import com.mitdroid.kenshi.Main.R;

import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.HashMap;

public class HijackerActivity extends Activity {

    public class SessionListAdapter extends ArrayAdapter<Session> {
        private int layoutId = 0;
        private HashMap<String, Session> sessions;

        public class FacebookUserTask extends AsyncTask<Session, Void, Boolean> {

            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param sessions The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected Boolean doInBackground(Session... sessions) {
                return null;
            }
        }

        public class XdaUserTask extends AsyncTask<Session, Void, Boolean> {

            /**
             * Override this method to perform a computation on a background thread. The
             * specified parameters are the parameters passed to {@link #execute}
             * by the caller of this task.
             * <p>
             * This method can call {@link #publishProgress} to publish updates
             * on the UI thread.
             *
             * @param sessions The parameters of the task.
             * @return A result, defined by the subclass of this task.
             * @see #onPreExecute()
             * @see #onPostExecute
             * @see #publishProgress
             */
            @Override
            protected Boolean doInBackground(Session... sessions) {
                return null;
            }
        }

        public class SessionHolder {

        }

        /**
         * @alert: the on request listener interface cannot be found
         * @Note: This class will be use without the interface.
         * TODO: write OnRequestListener interface
         */
        class RequestListener {

        }

        public SessionListAdapter(int layoutId) {
            super(HijackerActivity.this, layoutId);
            this.layoutId = layoutId;
            sessions = new HashMap<>();
        }

    }

    public static class Session {
        public Bitmap picture = null;
        public String username = null;
        public boolean inited = false;
        public boolean HTTPS = false;
        public String address = null;
        public String domain = null;
        public String useragent = null;
        public HashMap<String, BasicClientCookie> cookies = null;

        public Session() { cookies = new HashMap<>(); }

        public String getFilename() {
            String name = domain + "-" + (username != null ? username : address);
            return name.replaceAll("[ .\\\\/:*?\"<>|\\\\/:*?\"<>|]", "-");
        }
    }

    private ToggleButton hijackToggleButton;
    private ProgressBar hijackProgress;
    private ListView listView;
    private SessionListAdapter adapter;
    private boolean running;
    private SessionListAdapter.RequestListener requestListener;
    private SpoofSession spoofSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hijacker);
    }
}
