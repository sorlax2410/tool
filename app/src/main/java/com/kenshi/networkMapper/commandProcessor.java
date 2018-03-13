package com.kenshi.networkMapper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by kenshi on 01/02/2018.
 */

public class commandProcessor {

    public static String runCommand(String command, File currentDir) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command, null, currentDir);
        process.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        int read;
        char[]buffer = new char[4096];
        StringBuffer writer = new StringBuffer();
        StringBuffer error = new StringBuffer();

        while((read = reader.read(buffer)) > 0)
            writer.append(buffer, 0, read);
        while((read = errorReader.read(buffer)) > 0)
            error.append(buffer, 0, read);
        reader.close();
        errorReader.close();
        Log.d("errorTag", error.toString());

        return(writer.toString() + error.toString());
    }
}
