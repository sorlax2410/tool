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
        type = Type.fromString(reader.readLine());
        deviceType = reader.readLine();
        deviceType = deviceType.equals("null") ? null : deviceType;
        deviceOS = reader.readLine();
        deviceOS = deviceOS.equals("null") ? null : deviceOS;
        alias = reader.readLine();
        alias = alias.equals("null") ? null : alias;

        if(type == Type.NETWORK)
            return;
        else if(type == Type.ENDPOINT)
            endpoint = new Endpoint(reader);
        else if(type == Type.REMOTE) {
            hostname = reader.readLine();
            hostname = hostname.equals("null") ? null : hostname;
            if(hostname != null) {
                //This is needed to avoid NetworkOnMainThreadException
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                inetAddress = InetAddress.getByName(hostname);
            }
        }

        int ports = Integer.parseInt(reader.readLine());
        for(int index = 0; index < ports; index++) {
            String key = reader.readLine();
            String[]parts = key.split("\\|", 3);
            Port port = new Port(
                    Integer.parseInt(parts[1]),
                    NetworkChecker.Protocol.fromString(parts[0]),
                    parts[2]
            );

            this.ports.add(port);
            this.vulnerabilities.put(key, new ArrayList<Vulnerability>());

            int vulns = Integer.parseInt(reader.readLine());
            for(int inner = 0; inner < vulns; inner++) {
                Vulnerability vulnerability = new Vulnerability(reader);
                this.vulnerabilities.get(key).add(vulnerability);
            }
        }
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

    /**
     * @Description: getters and setters for alias
     */
    public void setAlias(String alias) { this.alias = alias; }
    public boolean hasAlias() { return (!alias.isEmpty() && alias != null); }
    public String getAlias() { return alias; }

    /**
     * @return: return network type
     */
    public Type getType() { return type; }

    /**
     * @Description:
     * @param stringBuilder:
     */
    public void serialize(StringBuilder stringBuilder) {
        stringBuilder.append(type + "\n");
        stringBuilder.append(deviceType + "\n");
        stringBuilder.append(deviceOS + "\n");
        stringBuilder.append(alias + "\n");

        //a network cannot be saved in a session file
        if(type == Type.NETWORK)    return;
        else if(type == Type.ENDPOINT)  endpoint.serialize(stringBuilder);
        else if(type == Type.REMOTE)    stringBuilder.append(hostname + "\n");
        stringBuilder.append(ports.size() + "\n");

        for(Port port: ports) {
            String key = port.toString();
            stringBuilder.append(key + "\n");
            if(vulnerabilities.containsKey(key)) {
                stringBuilder.append(vulnerabilities.get(key).size() + "\n");
                for(Vulnerability vulnerability : vulnerabilities.get(key))
                    stringBuilder.append(vulnerability.toString() + "\n");
            }
            else
                stringBuilder.append("0\n");
        }
    }

    /**
     * @Description:
     * @param target:
     * @return:
     */
    public boolean comesAfter(Target target) {
        if(type == Type.NETWORK)    return false;
        else if(type == Type.ENDPOINT) {
            if(target.getType() == Type.ENDPOINT)
                return endpoint.getAddressAsLong() > target.getEndpoint().getAddressAsLong();
            else
                return false;
        }
        else
            return true;
    }

    /**
     * @Description:
     * @param string:
     * @return:
     */
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

    /**
     * @Description:
     * @return:
     */
    public String getCommandLineRepresentation() { return "???"; }

    /**
     * @Description:
     * @param target:
     * @return:
     */
    public boolean equals(Target target) {
        if(type == target.getType()) {
            if(type == Type.NETWORK)
                return networkChecker.equals(target.getNetworkChecker());
            else if(type == Type.ENDPOINT)
                return endpoint.equals(target.getEndpoint());
            else if(type == Type.REMOTE)
                return hostname.equals(target.getHostname());
        }
        return false;
    }

    /**
     * @Description:
     * @param object:
     * @return:
     */
    public boolean equals(Object object) {
        if(object instanceof Target)
            return equals(object);
        else
            return false;
    }

    /**
     * @Description:
     * @return:
     */
    public String getDisplayAddress() {
        if(type == Type.NETWORK)
            return networkChecker.networkRepresentation();
        else if(type == Type.ENDPOINT)
            return endpoint.getInetAddress().getHostAddress() + (port == 0 ? "" : ":" + port);
        else if(type == Type.REMOTE)
            return hostname + (port == 0 ? "" : ":" + port);
        else
            return "???";
    }

    @Override
    public String toString() {
        if(hasAlias())
            return alias;
        else
            return getDisplayAddress();
    }

    /**
     * @Description:
     */
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

        /**
         * @Description:
         * @return:
         */
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

        /**
         * @Description:
         * @return: return String of identifier
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * @Description:
         * @param identifier:
         */
        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        /**
         * @Description:
         * @return:
         */
        public double getSeverity() {
            return severity;
        }

        /**
         * @Description:
         * @param severity:
         */
        public void setSeverity(double severity) { this.severity = severity; }

        /**
         * @Description: getters and setters for summary
         * @return: summary string
         */
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        @Override
        public String toString() {
            return identifier + "|" + severity + "|" + summary;
        }

        /**
         * @Description:
         * @return: html color
         */
        public String getHtmlColor() {
            if (severity < 0.5)
                return "#59FF00";
            else if (severity < 7)
                return "#FFD732";
            else
                return "#FF0000";
        }
    }

}
