package com.mitdroid.kenshi.mitdroid;

import android.util.Log;

import com.kenshi.networkMapper.commandProcessor;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PathUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void display(String[] parameter) {
        try {
            String workingDirectory = commandProcessor.runCommand("pwd",
                    binary.getAbsoluteFile());
            Log.d("Working directory", binary.getAbsolutePath());
            Log.d("Path", workingDirectory);
        }
        catch(IOException e) { Log.d(debugTag, e.getMessage()); }
        catch(InterruptedException e) { Log.d(debugTag, e.getMessage()); }

        for(int i = 0; i < binaries.length; i++) {
            try {
                String listDetail = commandProcessor.runCommand("ls -la " + binaries[i],
                        binary.getAbsoluteFile());

                Log.d("Listing Tag", "ls -la output: " + listDetail);
            }
            catch(IOException e) { Log.d(debugTag, e.getMessage()); }
            catch(InterruptedException e) { Log.d(debugTag, e.getMessage()); }
        }

        for(int i = 0; i < parameter.length; i++) { Log.d("Parameters", parameter[i]); }
    }

    @Test
    public void checkFile(String path) {
        File checker = new File(path);
        Log.d("Path", checker.getAbsolutePath());
        if (checker.exists())
            Log.d("Path", "Path available !!");

        for(int i = 0; i < binaries.length; i++) {
            checker = new File(path, binaries[i]);
            Log.d("Path", checker.getAbsolutePath());
            if (checker.exists())
                Log.d("Path", "Path available !!");
        }
    }

}