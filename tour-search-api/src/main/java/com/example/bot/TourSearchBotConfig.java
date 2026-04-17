package com.example.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "telegram")
public class TourSearchBotConfig {
    private String token;
    private String username;
    private String webAppUrl = "https://7d66d440c4fdebe9-185-15-189-159.serveousercontent.com";
}
