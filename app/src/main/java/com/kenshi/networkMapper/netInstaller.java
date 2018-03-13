package com.kenshi.networkMapper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.mitdroid.kenshi.mitdroid.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kenshi on 09/02/2018.
 */

public class netInstaller {

    private File binDirectory;
    private Context context;

    @SuppressLint("WrongConstant")
    public netInstaller(Context context) {
        this.context = context;
        binDirectory = this.context.getDir("bin", Context.MODE_MULTI_PROCESS);
    }

    public boolean installResources() {
        Resources resources = this.context.getResources();

        try {
            commandProcessor.runCommand("rm -rf ", binDirectory);

            InputStream inputStream = resources.openRawResource(R.raw.nmap);
            File writeFile = new File(binDirectory, "nmap");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_os_db);
            writeFile = new File(binDirectory, "nmap_os_db");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_payloads);
            writeFile = new File(binDirectory, "nmap_payloads");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_protocols);
            writeFile = new File(binDirectory, "nmap_protocols");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_rpc);
            writeFile = new File(binDirectory, "nmap_rpc");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_service_probes);
            writeFile = new File(binDirectory, "nmap_service_probes");
            moveBinaryResourceToFile(inputStream, writeFile);

            inputStream = resources.openRawResource(R.raw.nmap_services);
            writeFile = new File(binDirectory, "nmap_services");
            moveBinaryResourceToFile(inputStream, writeFile);

            String[]binaries = {
                    "nmap",
                    "nmap_os_db",
                    "nmap_payloads",
                    "nmap_protocols",
                    "nmap_rpc",
                    "nmap_service_probes",
                    "nmap_services"
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

    private void moveBinaryResourceToFile(InputStream inputStream, File outFile) throws
            IOException {
        byte[]buffer = new byte[1024];
        int byteCount;
        OutputStream outputStream = new FileOutputStream(outFile.getAbsolutePath());

        while((byteCount = inputStream.read(buffer)) > 0)
            outputStream.write(buffer, 0, byteCount);

        inputStream.close();
        outputStream.close();
    }
}
