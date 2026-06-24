package com.google.jenkins.plugins.computeengine;

import com.google.common.base.Strings;
import hudson.Util;
import hudson.util.Secret;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public final class GoogleApiProxyConfiguration {
    private final String host;
    private final Integer port;
    private final String username;
    private final Secret password;

    public GoogleApiProxyConfiguration(String host, Integer port, String username, Secret password) {
        this.host = Util.fixEmptyAndTrim(host);
        this.port = port;
        this.username = Util.fixEmptyAndTrim(username);
        this.password = password == null ? Secret.fromString(null) : password;
    }

    public static GoogleApiProxyConfiguration fromFormFields(
            String host, String port, String username, String password) {
        return new GoogleApiProxyConfiguration(host, parsePort(port), username, Secret.fromString(password));
    }

    public boolean isConfigured() {
        return !Strings.isNullOrEmpty(host);
    }

    public boolean hasAuthentication() {
        return !Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(Secret.toString(password));
    }

    public int getRequiredPort() {
        if (port == null) {
            throw new IllegalArgumentException("Proxy port must be set when proxy host is configured");
        }
        return port;
    }

    public String getPlainTextPassword() {
        return Secret.toString(password);
    }

    private static Integer parsePort(String port) {
        String normalizedPort = Util.fixEmptyAndTrim(port);
        if (normalizedPort == null) {
            return null;
        }
        try {
            return Integer.valueOf(normalizedPort);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
