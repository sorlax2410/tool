package com.kenshi.Core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.mitdroid.kenshi.Main.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class UpdateManager {

    private static final String tag = "UPDATE MANAGER";
    private static final String REMOTE_VERSION_URL = "";
    private static final String REMOTE_DOWNLLOAD_URL = "";
    private static final String VERSION_CHAR_MAP = "";

    private Context context = null;
    private String installedVersion = null;
    private String remoteVersion = null;

    public UpdateManager(Context context) {
        this.context = context;
        installedVersion = System.getAppVersionName();
    }

    private static double getVersionCode(String version) {
        String[]padded = new String[3],
                parts = version.split("[^0-9a-zA-z]");
        String item = "";
        StringBuilder digit = new StringBuilder(),
                letter = new StringBuilder();
        double code = 0,
                coeff = 0;
        int index, inner;
        char character;

        Arrays.fill(padded, 0, 3, "0");
        for(index = 0; index < Math.min(3, parts.length); index++)
            padded[index] = parts[index];
        for (index = padded.length - 1; index > -1; index--) {
            item = padded[index];
            coeff = Math.pow(10, padded.length - 1);

            if(item.matches("\\d+[a-zA-Z]")) {
                digit = new StringBuilder();
                letter = new StringBuilder();

                for (inner = 0; inner < item.length(); inner++) {
                    character = item.charAt(inner);
                    if(character >= '0' && character <= '9')
                        digit.append(character);
                    else
                        letter.append(character);
                }

                code += (
                        (Integer.parseInt(digit.toString()) + 1) * coeff -
                                ((VERSION_CHAR_MAP.indexOf(letter.toString().toLowerCase())
                                        + 1)/100.0)
                );
            }
            else if(item.matches("\\d+"))
                code += (Integer.parseInt(item) + 1) * coeff;
            else
                code += coeff;
        }
        return code;
    }

    public boolean isUpdateAvailable() {
        try {
            if(!installedVersion.isEmpty() && installedVersion != null) {
                if(remoteVersion != null && !remoteVersion.isEmpty()) {
                    URL url = new URL(REMOTE_VERSION_URL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(httpURLConnection.getInputStream())
                    );

                    String line = null;
                    StringBuilder buffer = new StringBuilder();

                    while((line = bufferedReader.readLine()) != null)
                        buffer.append(line).append("\n");
                    bufferedReader.close();
                    remoteVersion = buffer.toString().trim();
                }

                double installedVersionCode = getVersionCode(installedVersion),
                        remoteVersionCode = getVersionCode(remoteVersion);
                if(remoteVersionCode > installedVersionCode)
                    return true;
            }
        } catch (Exception e) { System.errorLogging(tag, e); }

        return false;
    }

    public String getRemoteVersion() { return remoteVersion; }
    public String getRemoteVersionFileName() { return "Mitdroid-" + remoteVersion + ".apk"; }
    public String getRemoteVersionUrl() { return REMOTE_VERSION_URL; }

    private String formatSize(int size) {
        if(size < 1024)
            return size + " B";
        else if(size < (1024*1024))
            return (size / 1024) + " KB";
        else if(size < (1024*1024*1024))
            return (size / (1024*1024)) + " MB";
        else
            return (size / (1024*1024*1024)) + " GB";
    }

    private String formatSpeed(int speed) {
        if(speed < 1024)
            return speed + " B/s";
        else if(speed < (1024*1024))
            return (speed / 1024) + " KB/s";
        else if(speed < (1024*1024*1024))
            return (speed / (1024*1024)) + " MB/s";
        else
            return (speed / (1024*1024*1024)) + " GB/s";
    }

    public boolean downloadUpdate(MainActivity activity, final ProgressDialog progressDialog) {
        try {
            HttpURLConnection.setFollowRedirects(true);
            URL url = new URL(getRemoteVersionUrl());
            File file = new File(System.getStoragePath());
            String filename = getRemoteVersionFileName();
            byte[]buffer = new byte[1024];
            int read = 0;
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            httpURLConnection.connect();

            file.mkdirs();
            file = new File(file, filename);
            if(file.exists())
                file.delete();

            FileOutputStream writer = new FileOutputStream(file);
            InputStream reader = httpURLConnection.getInputStream();
            int total = httpURLConnection.getContentLength(),
                    downloaded = 0,
                    sampled = 0;
            long time = java.lang.System.currentTimeMillis();
            double speed = 0.0,
                    deltat = 0.0;

            while(progressDialog.isShowing() && (read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
                downloaded += read;
                deltat = (java.lang.System.currentTimeMillis() - time) / 1000.0;

                if(deltat > 1.0) {
                    speed = (downloaded - sampled) / deltat;
                    time = java.lang.System.currentTimeMillis();
                    sampled = downloaded;
                }

                final int downloadedFiles = downloaded,
                        totalFiles = total;
                final double fileSpeed = speed;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage(
                                "[" + formatSpeed(((int) fileSpeed)) + "]" +
                                        formatSize(downloadedFiles) + "/" +
                                        formatSize(totalFiles) + ". . ."
                        );
                        progressDialog.setProgress( (100*downloadedFiles) / totalFiles);
                    }
                });
            }

            writer.close();
            reader.close();

            if(progressDialog.isShowing()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            else
                Log.d(tag, "DOWNLOAD CANCELED");

            return true;
        } catch (Exception e) { System.errorLogging(tag, e); }

        if(progressDialog.isShowing())
            progressDialog.dismiss();

        return false;
    }


}
