package com.toursearch.config;

import com.toursearch.bot.TourBot;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.service.LongPollingService;
import jakarta.annotation.PostConstruct;

@Configuration
public class TelegramBotConfig {

    private final TourBot tourBot;
    public TelegramBotConfig(TourBot tourBot) {
        this.tourBot = tourBot;
    }
    @PostConstruct
    public void initBot() {
        try {
            LongPollingService.registerBot(tourBot);
            System.out.println("Telegram bot registered successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Error registering Telegram bot: " + e.getMessage());
        }
    }
}