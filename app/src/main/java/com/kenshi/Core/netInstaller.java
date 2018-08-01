package com.kenshi.Core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.mitdroid.kenshi.mitdroid.R;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @Description: A class that handle the installation
 * Created by kenshi on 09/02/2018.
 */

public class netInstaller {

    private File binDirectory;
    private Context context;

    private final static String tag = "NETINSTALLER";
    private final static String filename = "nmap.zip";
    private final static String zipname = "nmap_zip.zip";
    private final static int BUFFER_SIZE = 4096;

    @SuppressLint("WrongConstant")
    public netInstaller(Context context) {
        this.context = context;
        binDirectory = this.context.getDir("bin", Context.MODE_MULTI_PROCESS);
    }

    /**
     * @Description: This function will install the resources to folder
     * @return: true if installed successfully, otherwise false
     */
    public boolean installResources() {
        Resources resources = this.context.getResources();

        try {
            commandProcessor.runCommand("rm _rf ", binDirectory);

            InputStream inputStream = resources.openRawResource(R.raw.nmap);
            File writeFile = new File(binDirectory, "nmap");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_os_db);
            writeFile = new File(binDirectory, "nmap-os-db");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_payloads);
            writeFile = new File(binDirectory, "nmap-payloads");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_protocols);
            writeFile = new File(binDirectory, "nmap-protocols");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_rpc);
            writeFile = new File(binDirectory, "nmap-rpc");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_service_probes);
            writeFile = new File(binDirectory, "nmap-service-probes");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_services);
            writeFile = new File(binDirectory, "nmap-services");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_mac_prefixes);
            writeFile = new File(binDirectory, "nmap-mac-prefixes");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_mac_prefixes);
            writeFile = new File(binDirectory, "nse_main.lua");
            moveBinaryResourceToFile(inputStream, writeFile);

            String[]binaries = {
                    "nmap",
                    "nmap_os_db",
                    "nmap_payloads",
                    "nmap_protocols",
                    "nmap_rpc",
                    "nmap_service_probes",
                    "nmap_services",
                    "nmap_mac_prefixes",
                    "nse_main.lua"
            };

            //change permission of all files in bin folder
            for(int i = 0; i < binaries.length; i++) {
                String log = commandProcessor.runCommand("chmod 7777 " + binaries[i],
                        binDirectory.getAbsoluteFile());

                String listDetail = commandProcessor.runCommand("ls -la " + binaries[i],
                        binDirectory.getAbsoluteFile());

                Log.d("DEBUG TAG(INSTALLATION)","chmod output: " + log);
                Log.d("Listing Tag", "ls -la output: " + listDetail);
            }
        }catch(IOException e) {
            Toast.makeText(context, "IOException!", Toast.LENGTH_LONG).show();
            Log.d("DEBUG TAG(IO EXCEPTION)", e.getMessage());
        }catch(InterruptedException e) {
            Toast.makeText(context, "InterruptedException!", Toast.LENGTH_LONG).show();
            Log.d("DEBUG TAG(INTERRUPTION)", e.getMessage());
        }
        return true;
    }

    /**
     * @Description: This function writes executable scripts into app_bin folder
     * @param inputStream:
     * @param outFile:
     * @throws IOException:
     */
    private void moveBinaryResourceToFile(@NonNull InputStream inputStream,
                                          @NonNull File outFile) throws
            IOException
    {
        byte[]buffer = new byte[1024];
        int byteCount;
        OutputStream outputStream = new FileOutputStream(outFile.getAbsolutePath());

        while((byteCount = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, byteCount);

        inputStream.close();
        outputStream.close();
    }

    public boolean installCompressedFile() {
        ZipInputStream zipInputStream;
        ZipEntry zipEntry;
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        FileOutputStream fileOutputStream;
        String filename,
                destination = context.getFilesDir().getAbsolutePath();

        try {
            zipInputStream = new ZipInputStream(
                    new BufferedInputStream(
                            context.getAssets()
                                    .open(this.filename)
                    )
            );

            while( (zipEntry = zipInputStream.getNextEntry()) != null) {
                filename = destination + "/" + zipEntry.getName();
                binDirectory = new File(destination + "/" + zipEntry.getName());

                if(zipEntry.isDirectory())
                    binDirectory.mkdirs();
                else {
                    fileOutputStream = new FileOutputStream(filename);

                    while((read = zipInputStream.read(buffer, 0, BUFFER_SIZE)) > -1)
                        fileOutputStream.write(buffer, 0, read);

                    fileOutputStream.close();
                    zipInputStream.closeEntry();
                }
            }

            zipInputStream.close();

            String command = "chmod ";

            commandProcessor.runCommand(command, binDirectory);
            return true;
        } catch(Exception e) { errorLogging(tag, e); return false; }
    }

    private void errorLogging(String tag, Exception exception) {
        String message = "Unknown error.",
                trace = "Unknown trace.",
                filename = (
                        new File(
                                Environment
                                        .getExternalStorageDirectory()
                                        .toString(),
                                "MitDroid-debug-error-log.log"
                        ).getAbsolutePath()
                );
        if(exception != null) {
            if(exception.getMessage() != null && !exception.getMessage().isEmpty())
                message = exception.getMessage();
            else if(exception.toString() != null)
                message = exception.toString();

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);

            exception.printStackTrace(printWriter);
            trace = writer.toString();

            if(context != null &&
                    getSettings().getBoolean("PREF_DEBUG_ERROR_LOGGING", false)
                    )
            {
                try {
                    FileWriter fileWriter = new FileWriter(filename, true);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    bufferedWriter.write(trace);
                    bufferedWriter.close();
                } catch (IOException ioe) { Log.e(tag, ioe.toString()); }
            }
        }

        Log.e(tag, message);
        Log.e(tag, trace);
    }

    private SharedPreferences getSettings() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
