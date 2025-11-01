package com.bellamyphan.finora_spring.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AppEnvironmentInfo {

    private final Environment environment;

    public AppEnvironmentInfo(Environment environment) {
        this.environment = environment;
    }

    /**
     * Builds a detailed string describing the app’s environment and host context.
     */
    public String buildInfo() {
        StringBuilder info = new StringBuilder();
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            String[] profiles = environment.getActiveProfiles();
            String profile = profiles.length > 0 ? profiles[0] : "default";

            info.append(String.format("Host: %s (%s)\nProfile: %s", hostname, hostAddress, profile));

            Map<String, String> envVars = getRelevantEnvironmentVariables();
            if (!envVars.isEmpty()) {
                info.append("\n\nDetected environment variables:\n");
                envVars.forEach((key, value) ->
                        info.append(String.format("%s = %s\n", key, value))
                );
            }

        } catch (Exception e) {
            info.append("Host information unavailable: ").append(e.getMessage());
        }

        return info.toString();
    }

    /**
     * Extracts environment variables that look like URLs, domains, or hosts.
     * Filters out potential secrets or tokens.
     */
    private Map<String, String> getRelevantEnvironmentVariables() {
        Map<String, String> result = new LinkedHashMap<>();
        System.getenv().forEach((key, value) -> {
            String lowerKey = key.toLowerCase();
            if ((lowerKey.contains("host") || lowerKey.contains("url") || lowerKey.contains("domain"))
                    && !lowerKey.matches(".*(key|token|secret|password).*")) {
                result.put(key, value);
            }
        });
        return result;
    }
}
