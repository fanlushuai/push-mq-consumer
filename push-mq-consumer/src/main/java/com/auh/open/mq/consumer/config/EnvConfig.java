package com.auh.open.mq.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    @Value("${spring.profiles.active:dev}")
    private String profiles;

    public boolean isDev() {
        if (isWWW()) {
            return false;
        }
        return profiles.contains("dev");
    }

    public boolean isWWW() {
        return profiles.contains("www") || profiles.contains("prod");
    }

    public boolean isLocal() {
        if (isWWW()) {
            return false;
        }
        return profiles.contains("local");
    }


}
