package com.toursearch.config;

import com.toursearch.bot.AdminBot;
import com.toursearch.tour_search_api.TourSearchController;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import jakarta.annotation.PostConstruct;

@Configuration
public class TelegramBotConfig {

    private final TourSearchController tourSearchController;
    private final AdminBot adminBot;

    @Value("${telegram.bot.token:}")
    private String tourBotToken;

    public TelegramBotConfig(TourSearchController tourSearchController, AdminBot adminBot) {
        this.tourSearchController = tourSearchController;
        this.adminBot = adminBot;
    }

    @PostConstruct
    public void initBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            if (tourBotToken != null && !tourBotToken.isEmpty()) {
                botsApi.registerBot(new com.toursearch.bot.TourBot(tourSearchController, tourBotToken));
                System.out.println("Tour bot registered successfully!");
            } else {
                System.out.println("Tour bot token not set, skipping");
            }

            if (adminBot.getBotToken() != null && !adminBot.getBotToken().isEmpty()) {
                botsApi.registerBot(adminBot);
                System.out.println("Admin bot registered successfully!");
            } else {
                System.out.println("Admin bot token not set, skipping registration");
            }
        } catch (TelegramApiException e) {
            System.err.println("Error registering bots: " + e.getMessage());
        }
    }
}