package com.kenshi.NetworkManager;

import android.os.StrictMode;
import android.util.Log;

import com.kenshi.Core.System;

import java.io.BufferedReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Target {
    private static final String tag = "TARGET";
    private NetworkChecker networkChecker = null;
    private Endpoint endpoint = null;
    private int port = 0;
    private String hostname = null;
    private Type type = null;
    private InetAddress inetAddress = null;
    private List<Port>ports = new ArrayList<>();
    private String deviceType = null;
    private String deviceOS = null;
    private String alias = null;
    private HashMap<String, ArrayList<Vulnerability>>vulnerabilities = new HashMap<>();


    public Target(Endpoint endpoint) { setEndpoint(endpoint); }

    public Target(String hostname, int port) { setHostname(hostname, port); }

    public Target(NetworkChecker networkChecker) { setNetworkChecker(networkChecker); }

    public Target(InetAddress inetAddress, byte[]hardware) { setEndpoint(inetAddress, hardware); }

    public Target(BufferedReader reader) throws Exception {

    }

    /**
     * @Description: getters and setters for hostname
     */
    public void setHostname(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        type = Type.REMOTE;

        try {
            //This is needed to avoid NetworkOnMainThreadException
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            inetAddress = InetAddress.getByName(this.hostname);
        } catch (Exception e) { Log.d("Target.setHostname()", e.toString()); }
    }
    public String getHostname() { return hostname; }

    /**
     * @Description: getters and setters for port
     */
    public void setPort(int port) { this.port = port; }
    public int getPort() { return port; }

    /**
     * @Description: getters and setters for networkChecker
     */
    public void setNetworkChecker(NetworkChecker networkChecker) {
        this.networkChecker = networkChecker;
        type = Type.NETWORK;
    }
    public NetworkChecker getNetworkChecker() { return networkChecker; }

    /**
     * @Description: getters and setters for endpoint
     */
    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        type = Type.ENDPOINT;
    }
    public Endpoint getEndpoint() { return endpoint; }
    public void setEndpoint(InetAddress inetAddress, byte[]hardware) {
        endpoint = new Endpoint(inetAddress, hardware);
        type = Type.ENDPOINT;
    }


    public enum Type {
        NETWORK,
        ENDPOINT,
        REMOTE;

        public static Type fromString(String type) throws Exception {
            if(!type.isEmpty()) {
                type = type.trim().toLowerCase();
                if(type.equals("network"))
                    return NETWORK;
                else if(type.equals("endpoint"))
                    return ENDPOINT;
                else
                    return REMOTE;
            }
            throw new Exception("Cannot deserialize target from string.");
        }
    }

    public static class Port {
        public NetworkChecker.Protocol protocol;
        public int port;
        public String service;

        public Port(int port, NetworkChecker.Protocol protocol, String service) {
            this.port = port;
            this.protocol = protocol;
            this.service = !service.isEmpty() ? (service.equals("null") ? null : service) : null;
        }
        public Port(int port, NetworkChecker.Protocol protocol) { this(port, protocol, null); }

        public String getServiceQuery() {
            String query = "";

            if(!service.isEmpty()) {
                query = service;

                //remove version number
                query = query.replaceAll("[\\d\\.]+", " ");

                //remove everything but letters, digit uppers and under scores
                query = query.replaceAll("^[a-zA-Z0-9\\-_]", " ");

                //remove multiple spaces
                query = query.replaceAll("[\\s]{2,}", " ");

                //trim
                query = query.trim();
            }

            return query;
        }

        @Override
        public String toString() {
            return protocol.toString() + "|" + port + "|" + service;
        }
    }

    public static class Vulnerability {
        private String identifier = null;
        private double severity = 0;
        private String summary = null;

        public Vulnerability() {}

        public Vulnerability(BufferedReader reader) throws Exception {
            String serialize = reader.readLine();
            String[] parts = serialize.split("\\|", 3);

            identifier = parts[0];
            severity = Double.parseDouble(parts[1]);
            summary = parts[2];
        }


        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public double getSeverity() {
            return severity;
        }

        public void setSeverity(double severity) {
            this.severity = severity;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        @Override
        public String toString() {
            return identifier + "|" + severity + "|" + summary;
        }

        public String getHtmlColor() {
            if (severity < 0.5)
                return "#59FF00";
            else if (severity < 7)
                return "#FFD732";
            else
                return "#FF0000";
        }
    }

    public static Target getFromString(String string) {
        final Pattern
                PARSE_PATTERN = Pattern
                        .compile("^(([a-z]+)://)?([0-9a-z\\-\\.]+)" +
                            "(:([\\d]+))?[0-9a-z\\-\\./]*$", Pattern.CASE_INSENSITIVE),

                IP_PATTERN = Pattern
                        .compile("^[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}$");

        Matcher matcher = null;
        Target target = null;

        try {
            string = string.trim();

            if((matcher = PARSE_PATTERN.matcher(string)) != null && matcher.find()) {
                String protocol = matcher.group(2),
                        address = matcher.group(3),
                        ports = matcher.group(4);

                protocol = !protocol.isEmpty() ? protocol.toLowerCase() : null;
                ports = !ports.isEmpty() ? ports.toLowerCase() : null;

                if(!address.isEmpty()) {
                    //attemp to get the port from the protocol or the specified one
                    int port = 0;

                    if(ports != null)
                        port = Integer.parseInt(ports);
                    else if(protocol != null)
                        port = System.getPortByProtocol(protocol);

                    //determine if the "address" part is an ip address or a host name
                    if(IP_PATTERN.matcher(address).find()) {
                        //internal ip address
                        if(System.getNetwork().isInternal(address)) {
                            target = new Target(new Endpoint(address));
                            target.setPort(port);
                        }
                        //external ip address, return as host name
                        else
                            target = new Target(address, port);
                    }

                    //found a hostname
                    else
                        target = new Target(address, port);
                }
            }
        } catch (Exception e) { System.errorLogging(tag, e); }

        //determine if target is reachable
        if(target != null) {
            try {
                //This is needed to avoid NetworkOnMainThreadException
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Log.d(tag + " getFromString", InetAddress
                        .getByName(target.getCommandLineRepresentation())
                        .toString()
                );
            } catch (Exception e) { target = null; }
        }

        return target;
    }

    public String getCommandLineRepresentation() { return "???"; }
}
