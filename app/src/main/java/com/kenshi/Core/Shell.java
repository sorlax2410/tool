package com.kenshi.Core;


import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class Shell {
    private static final String tag = "SHELL";

    /**
     * @Description:
     */
    public interface OutputReceiver {
        void onStart(String command);
        void onNewLine(String command);
        void onEnd(int exitCode);
    }

    /**
     * @Description: A <code>StreamGobbler</code> is an InputStream that uses an internal worker
     * thread to constantly consume input from another InputStream. It uses a buffer
     * to store the consumed data. The buffer size is automatically adjusted, if needed.
     * <p/>
     * This class is sometimes very convenient - if you wrap a session's STDOUT and STDERR
     * InputStreams with instances of this class, then you don't have to bother about
     * the shared window of STDOUT and STDERR in the low level SSH-2 protocol,
     * since all arriving data will be immediatelly consumed by the worker threads.
     * Also, as a side effect, the streams will be buffered (e.g., single byte
     * read() operations are faster).
     * <p/>
     * Other SSH for Java libraries include this functionality by default in
     * their STDOUT and STDERR InputStream implementations, however, please be aware
     * that this approach has also a downside:
     * <p/>
     * If you do not call the StreamGobbler's <code>read()</code> method often enough
     * and the peer is constantly sending huge amounts of data, then you will sooner or later
     * encounter a low memory situation due to the aggregated data (well, it also depends on the Java heap size).
     * Joe Average will like this class anyway - a paranoid programmer would never use such an approach.
     * <p/>
     * The term "StreamGobbler" was taken from an article called "When Runtime.exec() won't",
     * see http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html.
     */
    private static class StreamGobbler extends Thread {
        private BufferedReader reader = null;
        private OutputReceiver receiver = null;

        public StreamGobbler(BufferedReader reader, OutputReceiver receiver) {
            this.reader = reader;
            this.receiver = receiver;
            setDaemon(true);
        }

        public void run() {
            try {
                while(true) {
                    String line = "";
                    if(reader.ready()) {
                        if ((line = reader.readLine()) == null)
                            continue;
                    }
                    else {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {}
                        continue;
                    }
                    if(!line.isEmpty() && receiver != null)
                        receiver.onNewLine(line);
                }
            } catch (IOException e) { System.errorLogging(tag, e); }
            finally {
                try { reader.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    private static Process spawnShell(String command,
                                      boolean updateLibraryPath,
                                      boolean redirectErrorStream)
            throws IOException
    {

        ProcessBuilder builder = new ProcessBuilder().command(command);
        Map<String, String>environment = builder.environment();

        builder.redirectErrorStream(redirectErrorStream);
        if(updateLibraryPath) {
            boolean found = false;
            String libPath = System.getLibraryPath();

            for(Map.Entry<String, String>entry:environment.entrySet()) {
                if (entry.getKey().equals("LD_LIBRARY_PATH")) {
                    environment.put("LD_LIBRARY_PATH", entry.getValue() + ":" + libPath);
                    found = true;
                    break;
                }
            }

            if(!found)
                environment.put("LD_LIBRARY_PATH", libPath);
        }
        return builder.start();
    }

    private static Process spawnShell(String command)
            throws IOException {
        return spawnShell(command,
                false,
                true
        );
    }

    public static boolean isRootGranted() {
        Process process = null;
        DataOutputStream writer = null;
        BufferedReader reader = null;
        String line = null;
        boolean granted = false;

        try {
            process = spawnShell("su");
            writer = new DataOutputStream(process.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            writer.writeBytes("id\n");
            writer.flush();
            writer.writeBytes("exit\n");
            writer.flush();
            while (!(line = reader.readLine()).isEmpty() && !granted)
                if(line.toLowerCase().contains("uid=0"))
                    granted = true;
            process.waitFor();

        } catch (Exception e) { System.errorLogging(tag, e); }

        finally {
            try {
                if(writer != null)
                    writer.close();
                if (reader != null)
                    reader.close();
            } catch (IOException ignored) {}
        }
        return granted;
    }

    public static boolean isBinaryAvailable(String binary) {
        try {
            Process process = spawnShell("sh");
            DataOutputStream writer = new DataOutputStream(process.getOutputStream());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()
                    )
            );
            String line = null;

            writer.writeBytes("which " + binary + "\n");
            writer.close();
            writer.writeBytes("exit\n");
            writer.close();

            while(!(line = reader.readLine()).isEmpty())
                if(line.startsWith("/"))
                    return true;
        } catch (Exception e) { System.errorLogging(tag, e); }
        return false;
    }

    public static boolean isLibraryPathOverridable(Context context) {
        boolean linkerError = false;
        try {
            String libPath = System.getLibraryPath(),
                    filename = context.getFilesDir().getAbsolutePath() + "/tools/nmap/nmap";
            File file = new File(filename);
            String dirName = file.getParent(),
                    comamnd = "cd " + dirName + " && ./nmap --version";
            Process process = spawnShell("su", true, false);
            DataOutputStream writer = new DataOutputStream(process.getOutputStream());
            BufferedReader
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream())),
                    stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = null, error = null;

            writer.writeBytes("export LD_LIBRARY_PATH=" + libPath + ":$LD_LIBRARY_PATH\n");
            writer.flush();
            writer.writeBytes(comamnd + "\n");
            writer.flush();
            writer.writeBytes("exit\n");
            writer.flush();

            while(!(line = reader.readLine()).isEmpty() && !linkerError)
                if(line.contains("CANNOT LINK EXECUTABLE"))
                    linkerError = true;
            process.waitFor();
        } catch (Exception e) { System.errorLogging(tag, e); return !linkerError; }
        return !linkerError;
    }

    public static int exec(String comand, OutputReceiver receiver, boolean overrideLibraryPath)
        throws IOException, InterruptedException
    {
        Process process = spawnShell("su", overrideLibraryPath, false);

        if(receiver != null)
            receiver.onStart(comand);

        DataOutputStream writer = new DataOutputStream(process.getOutputStream());
        BufferedReader
                reader = new BufferedReader(new InputStreamReader(process.getInputStream())),
                error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String libPath = System.getLibraryPath();
        int exit = -1;

        if(overrideLibraryPath) {
            writer.writeBytes("export LD_LIBRARY_PATH=" + libPath + ":$LD_LIBRARY_PATH\n");
            writer.flush();
        }

        // split cd working-directory && ./command
        if(comand.startsWith("cd /") && comand.contains("&&")) {
            String[]split = comand.split("&&", 2);

            if(split.length == 2) {
                writer.writeBytes(split[0] + "\n");
                writer.flush();
                comand = split[1].trim();
            }
        }

        try {
            writer.writeBytes(comand + "\n");
            writer.flush();

            StreamGobbler outGobbler = new StreamGobbler(reader, receiver),
                    errGobbler = new StreamGobbler(error, receiver);

            outGobbler.start();
            errGobbler.start();
            writer.writeBytes("exit\n");
            writer.flush();

            /*
             * The following catastrophe of code seems to be the best way to ensure
             * this thread can be interrupted.
             */
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    exit = process.exitValue();
                    Thread.currentThread().interrupt();
                } catch(IllegalThreadStateException e) {
                    /*
                     * Just sleep, the process hasn't terminated yet but sleep should (but doesn't) cause
                     * InterruptedException to be thrown if interrupt() has been called.
                     *
                     * .25 seconds seems reasonable
                     */
                    Thread.sleep(250);
                }
            }
        } catch (IOException e) { System.errorLogging(tag, e); }
        catch (InterruptedException e) {
            try { // key to kill executable and process
                writer.close();
                reader.close();
                error.close();
            } catch (IOException ignore) {/*swallow error*/}
        }

        if(receiver != null)
            receiver.onEnd(exit);

        return exit;
    }

    public static int exec(String command, OutputReceiver receiver)
        throws IOException, InterruptedException
    {
        return exec(command, receiver, true);
    }

    public static int exec(String command) throws IOException, InterruptedException {
        return exec(command, null, true);
    }

    public static Thread async(final String command,
                               final OutputReceiver receiver,
                               final boolean overrideLibraryPath)
    {
        Thread launcher = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try { exec(command, receiver, overrideLibraryPath); }
                        catch (Exception e) { System.errorLogging(tag, e); }
                    }
                }
        );

        launcher.setDaemon(true);
        return launcher;
    }

    public static Thread async(String command) { return async(command, null, true); }
    public static Thread async(final String command, final OutputReceiver receiver) { return async(command, receiver, true); }

}
