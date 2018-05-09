package com.kenshi.networkMapper;

import android.util.Log;

import java.util.ArrayList;

public class stringSplitter {

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

    public static String splitHost(String host) {
        String test = host.substring(
                6,
                countString(host)
        );
        Log.d("SPLIT HOST TEST", test);
        return test;
    }

    private static int countString(CharSequence string) {
        int countedValue;
        for(countedValue = 7; countedValue < string.length(); countedValue++) {
            if(string.charAt(countedValue) == ' ') break;
        }
        return countedValue;
    }

    public static ArrayList<ArrayList<String>> joinArrays(ArrayList<String> MACAddress,
                                                          ArrayList<String> targets) {
        ArrayList<ArrayList<String>> joiner = new ArrayList<>();
        joiner.add(targets);
        joiner.add(MACAddress);
        return joiner;
    }

    public static ArrayList<String> splitIPV4(String log) {
        ArrayList<String> targets = new ArrayList<>();
        String[]string = log.split("Nmap scan report for ");

        for(int index = 1; index < string.length; index++)
            targets.add(string[index]);

        string = targets.toString().split("\\n");
        targets.trimToSize();
        targets.clear();

        for(int index = 0; index < string.length - 4; index++)
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

    public static ArrayList<String> splitManufacturer(String log) {
        ArrayList<String>container = new ArrayList<>();
        String[]string = log.split("\\n");
        for(int index = 0; index < string.length; index++)
            if(index % 2 != 0)
                container.add(string[index]);

        string = container.toString().split("\\p{P}");
        container.trimToSize();
        container.clear();
        for(int index = 0; index < string.length; index++)
            if(index % 2 == 0)
                container.add(string[index]);

        string = container.toString().split("\\p{P}");
        container.trimToSize();
        container.clear();
        for(int index = 0; index < string.length; index++)
            if(index % 2 != 0)
                container.add(string[index]);

        for(int index = 0; index < container.size(); index++)
            Log.d("Split manufacturer test", container.get(index));
        return container;
    }

}
