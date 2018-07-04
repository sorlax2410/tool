package com.kenshi.networkMapper;

import android.util.Log;

import java.util.ArrayList;

/**
 * @Description: This helper class extract useful information during the initial scan
 */

public class stringSplitter {

    /**
     * Description: split the scanned local network and excluding the nmap' origin information
     * the function is used for displaying targets
     * @param log: The scanned log written by scanner
     * @return: return the original ip addresses and extra information without excluding anything
     */

    public static ArrayList<String> splitLine(String log) {
        ArrayList<String> targets = new ArrayList<>();
        String[]container = log.split("\\n");
        int limiter = container.length - 2;

        for(int index = 0; index < container.length; index++)
            Log.d("LINE FULL INFO " + String.valueOf(index), container[index]);

        for(int index = 2; index < limiter; index++)
            targets.add(container[index]);

        for(int index = 0; index < targets.size(); index++)
            Log.d("SPLIT LINE TEST " + String.valueOf(index), targets.get(index));
        return targets;
    }

    /**
     * Description: splitting the formatted hosts
     * Note: This function is used for obtaining target's ip address
     * @param host: The overall formatted hosts scanned by the scanner
     * @return: return the array of hosts
     */

    public static String splitHost(String host) {
        return host.substring(6, countString(host));
    }

    /**
     * Description: counting the characters of ip address until a space is met
     * @param string: a formatted ip address(including "Status:" )
     * @return: return the number of ip address characters
     */

    private static int countString(CharSequence string) {
        int countedValue;
        for(countedValue = 7; countedValue < string.length(); countedValue++) {
            if(string.charAt(countedValue) == ' ') break;
        }
        return countedValue;
    }

    /**
     * Description: This function is for scanning local network without formatting it
     * @param log The overall log written by the scanner
     * @return return the array of hosts
     */

    public static ArrayList<String> splitIPV4(String log) {
        ArrayList<String> targets = new ArrayList<>();
        String[]string = log.split("Nmap scan report for ");

        /**
         * Description: splitting the junk of string into arrays of string excluding the above
         * string, the first index describes nmap's origin
         */
        for(int index = 1; index < string.length; index++)
            targets.add(string[index]);

        string = targets.toString().split("\\n");
        targets.trimToSize();
        targets.clear();

        /**
         * Description: fetching the victim's ip address i a network and excluding the default
         * gateway and the attacker's ip address
         *
         * Note:
         * 0 is the default gateway
         * length - 5 is the attacker's ip address
         */

        for(int index = 1; index < string.length - 5; index++)
            if(index % 3 == 0)
                targets.add(string[index]);

        String replacement;
        replacement = targets.toString()
                .replaceAll("\\[", "")
                .replaceAll("]", "");
        string = replacement.split(", ");
        targets.trimToSize();
        targets.clear();

        for(int index = 0; index < string.length; index++)
            if(!string[index].equals(""))
                targets.add(string[index]);
        return targets;
    }

    /**
     * Description: split the MAC address of the victims only
     * @param log: The overall log written by scanner
     * @return: return the array of MAC Addresses
     */
    public static ArrayList<String> splitManufacturer(String log) {
        ArrayList<String>container = new ArrayList<>();
        String[]string = log.split("\\n");
        CharSequence charSequence;
        /**
         * Description: find the MAC address and split it out.
         * Note:
         * 0 contains nothing
         */
        for(int index = 1; index < string.length; index++) {
            charSequence = string[index];
            if(charSequence.charAt(0) == 'M')
                container.add(string[index]);
        }
        return container;
    }

    /**
     * @Description: turn a formatted custom flag into raw custom flag
     * @param customFlag: chosen flags
     * @return String: return the raw flag
     */
    public static String breakdownCustomFlags(ArrayList<String>customFlag) {
        String flagsConcat = customFlag.toString();
        Log.d("CUSTOM FLAG", customFlag.toString());
        String[] container = flagsConcat.split("[\\[,\\]]");
        flagsConcat = "";
        for(int index = 0; index < container.length; index++) {
            Log.d("CONTAINER", container[index]);
            flagsConcat.concat(" " + container[index]);
            Log.d("FLAGCONCAT", flagsConcat);
        }
        return flagsConcat;
    }

    /**
     * @Description: split the odd ports opened by the target
     * @param log: Captured log
     * @return: Services used by the target
     */
    public static ArrayList<String> splitPorts(String log) {
        String[] container = log.split("[\n/]");
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> ports = new ArrayList<>();

        for(int index = 0; index < container.length; index++)
            Log.d("SPLITPORTS " + index, container[index]);

        try {
            for (int index = 8; index < container.length; index++) {
                if (index % 2 == 0 &&
                        !container[index].contains("MAC") &&
                        !container[index].contains("Nmap"))
                    ports.add(container[index]);
            }

            for (int index = 0; index < ports.size(); index++)
                Log.d("SPITTED PORT " + index, ports.get(index));

            for (int index = 0; index < ports.size(); index++)
                if (Integer.parseInt(ports.get(index)) > 10000)
                    result.add(ports.get(index));

            for (int index = 0; index < result.size(); index++)
                Log.d("RESULT " + index, result.get(index));

            Log.d("RESULT", String.valueOf(result));
        } catch (Exception e) { Log.d("EXCEPTION", "NO PORTS ARE OPEN"); }
        return result;
    }

}
