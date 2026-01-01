package com.vi5hnu.codesprout.services;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService {

    private final Environment env;

    public EnvironmentService(Environment env) {
        this.env = env;
    }

    public void printActiveProfile() {
        String[] activeProfiles = env.getActiveProfiles();
        System.out.println("Active profiles: " + String.join(",", activeProfiles));
    }

    public boolean isProd() {
        String[] activeProfiles = env.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("prod".equalsIgnoreCase(profile)) return true;
        }
        return false;
    }
}
