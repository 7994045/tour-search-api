package com.toursearch.config;

import com.toursearch.bot.AdminBot;
import com.toursearch.bot.TourBot;
import com.toursearch.service.AdminNotificationService;
import com.toursearch.service.StatsService;
import com.toursearch.tour_search_api.TourSearchController;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import jakarta.annotation.PostConstruct;

@Configuration
public class TelegramBotConfig {

    private final TourSearchController tourSearchController;
    private final AdminBot adminBot;
    private final StatsService statsService;
    private final AdminNotificationService notificationService;

    public TelegramBotConfig(TourSearchController tourSearchController, AdminBot adminBot,
                             StatsService statsService, AdminNotificationService notificationService) {
        this.tourSearchController = tourSearchController;
        this.adminBot = adminBot;
        this.statsService = statsService;
        this.notificationService = notificationService;
    }

    @PostConstruct
    public void initBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TourBot(tourSearchController, statsService, notificationService));
            botsApi.registerBot(adminBot);
            System.out.println("Telegram bots registered successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Error registering Telegram bots: " + e.getMessage());
        }
    }
}