package ru.homyakin.gwent.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("ru.homyakin.gwent.telegram.bot")
public class BotConfiguration {
    private String token;
    private String username;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
