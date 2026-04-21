package com.toursearch.config;

import com.toursearch.tour_search_api.TourSearchController;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import jakarta.annotation.PostConstruct;

@Configuration
public class TelegramBotConfig {

    private final TourSearchController tourSearchController;

    public TelegramBotConfig(TourSearchController tourSearchController) {
        this.tourSearchController = tourSearchController;
    }

    @PostConstruct
    public void initBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new com.toursearch.bot.TourBot(tourSearchController));
            System.out.println("Telegram bot registered successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Error registering Telegram bot: " + e.getMessage());
        }
    }
}