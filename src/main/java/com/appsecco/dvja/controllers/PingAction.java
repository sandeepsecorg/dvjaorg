package com.appsecco.dvja.controllers;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class PingAction extends BaseController {

    private String address;
    private String commandOutput;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommandOutput() {
        return commandOutput;
    }

    public void setCommandOutput(String commandOutput) {
        this.commandOutput = commandOutput;
    }

    public String execute() {
        if(StringUtils.isEmpty(getAddress()))
            return INPUT;

        if (!isValidAddress(getAddress())) {
            addActionMessage("Invalid IP address or hostname.");
            return INPUT;
        }

        try {
            doExecCommand();
        } catch (Exception e) {
            addActionMessage("Error running command: " + e.getMessage());
        }

        return SUCCESS;
    }

    private void doExecCommand() throws IOException {
        // It is assumed the address is already validated at this point
        String[] command = { "ping", "-t", "5", "-c", "5", getAddress() };
        Process process = Runtime.getRuntime().exec(command);

        BufferedReader  stdinputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        String output = "Output:\n\n";

        while((line = stdinputReader.readLine()) != null)
            output += line + "\n";

        output += "\n";
        output += "Error:\n\n";

        stdinputReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while((line = stdinputReader.readLine()) != null)
            output += line + "\n";

        setCommandOutput(output);
    }
    // Only allows valid IPv4/IPv6 addresses or hostnames (no spaces or metacharacters)
    private boolean isValidAddress(String input) {
        if (input == null) return false;
        // IPv4 pattern
        String ipv4 = "^(?:(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(?:25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        // IPv6 (very simple pattern)
        String ipv6 = "^[0-9a-fA-F:]+$";
        // Hostname: per RFC 952 and 1123
        String hostname = "^[a-zA-Z0-9.-]{1,253}$";

        return Pattern.matches(ipv4, input)
            || Pattern.matches(ipv6, input)
            || Pattern.matches(hostname, input);
    }
}
