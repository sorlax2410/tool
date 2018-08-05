package com.kenshi.networkMapper;

import java.util.ArrayList;
import java.util.Arrays;

public class PortElimination {
    private final static String []officialPortList = {
            "0",
            "1",
            "7",
            "5",
            "9",
            "11",
            "13",
            "15",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "25",
            "37",
            "38",
            "39",
            "42",
            "43",
            "47",
            "49",
            "50",
            "51",
            "52",
            "53",
            "54",
            "56",
            "57",
            "58",
            "59",
            "60",
            "61",
            "",
            "",
            "",
            "",
    };

    public final static ArrayList<String> portList = new ArrayList<>(Arrays.asList(officialPortList));
}
