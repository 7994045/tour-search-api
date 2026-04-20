package com.toursearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.client.OkHttpTelegramBotClient;
import org.telegram.telegrambots.service.LongPollingService;
import org.telegram.telegrambots.session.LongPollingSession;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.token}")
    private String botToken;

    @PostConstruct
    public void initBot() {
        try {
            OkHttpTelegramBotClient botClient = new OkHttpTelegramBotClient(botToken);
            
            List<BotCommand> commands = new ArrayList<>();
            commands.add(new BotCommand("/start", "Запустить бота"));
            commands.add(new BotCommand("/search", "Поиск туров"));
            commands.add(new BotCommand("/help", "Помощь"));
            
            SetMyCommands setMyCommands = new SetMyCommands();
            setMyCommands.setCommands(commands);
            setMyCommands.setScope(new BotCommandScopeDefault());
            
            botClient.execute(setMyCommands);
            
            System.out.println("Telegram bot initialized successfully!");
        } catch (TelegramApiException e) {
            System.err.println("Error initializing Telegram bot: " + e.getMessage());
        }
    }
}
