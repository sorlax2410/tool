package com.kenshi.Core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by kenshi on 09/02/2018.
 */

public class netInstaller {

    private String binDirectory;
    private Context context;

    private final String toolFilename = "tools.zip";
    private final static String tag = "INSTALLER";
    private final static int bufferSize = 4096;
    private final static String[] permissionCommands = {
            "chmod 777 {PATH}/*/",
            "chmod 777 {PATH}/ettercap/ --recursive",
            "chmod 755 {PATH}/ettercap/ettercap",
            "chmod 755 {PATH}/ettercap/etterfilter",
            "chmod 755 {PATH}/ettercap/etterlog",
            "chmod 755 {PATH}/nmap/nmap",
            "chmod 755 {PATH}/arpspoof/arpspoof",
            "chmod 755 {PATH}/tcpdump/tcpdump",
            "chmod 666 {PATH}/hydra/ --recursive",
            "chmod 755 {PATH}/hydra/hydra",
            "mount -o remount,rw /system /system" +
                    " && ( chmod 6755 /system/*/su;" +
                    " mount -o remount,ro /system /system )",
            "chmod 755 {FILES}"
    };


    public final static String[] toolname = {
            "ettercap",
            "nmap",
            "arpspoof",
            "tcpdump",
            "hydra"
    };

    @SuppressLint("WrongConstant")
    public netInstaller(Context context) {
        this.context = context;
        binDirectory = this.context.getFilesDir().getAbsolutePath();
    }

    public boolean installResources() {
        ZipInputStream zipInputStream;
        ZipEntry zipEntry;
        byte[] bytes = new byte[bufferSize];
        int read;
        FileOutputStream fileOutputStream;
        File file;
        String filename, path;

        try {
            zipInputStream = new ZipInputStream(
                    new BufferedInputStream(
                            context.getAssets()
                                    .open(toolFilename)
                    )
            );
            StringBuilder command = null;

            while((zipEntry = zipInputStream.getNextEntry()) != null) {
                filename = path = binDirectory + "/" + zipEntry.getName();
                file = new File(path);
                if(zipEntry.isDirectory())
                    file.mkdirs();
                else {
                    fileOutputStream = new FileOutputStream(filename);
                    while((read = zipInputStream.read(bytes, 0, bufferSize)) > -1)
                        fileOutputStream.write(bytes, 0, read);
                    fileOutputStream.close();
                    zipInputStream.closeEntry();
                }
            }
            zipInputStream.close();

            for(String installCommand: permissionCommands)
                command.append(installCommand
                                .replace("{PATH}", binDirectory + "/tools")
                                .replace("{FILES}", binDirectory)
                                .concat("; ")
                );
            commandProcessor.runCommand(command);
            return true;
        }catch(IOException e) {
            Toast.makeText(context, "IOException!", Toast.LENGTH_LONG).show();
            Log.d("DEBUG TAG(IO EXCEPTION)", e.getMessage());
            return false;
        }catch(InterruptedException e) {
            Toast.makeText(context, "InterruptedException!", Toast.LENGTH_LONG).show();
            Log.d("DEBUG TAG(INTERRUPTION)", e.getMessage());
            return false;
        }catch (Exception e) {
            Log.e("Exception", e.getMessage());
            return false;
        }
    }
}
