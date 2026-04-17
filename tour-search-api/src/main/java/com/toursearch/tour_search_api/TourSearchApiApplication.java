package com.toursearch.tour_search_api;

import com.toursearch.TourSearchBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication(scanBasePackages = "com.toursearch")
public class TourSearchApiApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TourSearchApiApplication.class, args);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TourSearchBot bot = context.getBean(TourSearchBot.class);
            botsApi.registerBot(bot);
            System.out.println("Bot started!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}


