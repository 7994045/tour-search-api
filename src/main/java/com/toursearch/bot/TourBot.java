package com.toursearch.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramBotClient;
import org.telegram.telegrambots.client.okhttp.OkHttpBotSessionProvider;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

@Component
public class TourBot implements LongPollingBot {

    private final OkHttpTelegramBotClient botClient;
    private final String botToken = "YOUR_TOKEN_HERE";

    public TourBot() {
        this.botClient = new OkHttpTelegramBotClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            
            String response = processMessage(messageText);
            sendMessage(chatId, response);
        }
    }

    private String processMessage(String message) {
        return switch (message) {
            case "/start" -> "Привет! Я бот для поиска туров. Используй /search для поиска.";
            case "/help" -> "Доступные команды:\n/start - Запуск\n/search - Поиск туров\n/help - Помощь";
            case "/search" -> "Введи страну для поиска туров (например: Турция, Египет, ОАЭ)";
            default -> "Не понимаю. Используй /help";
        };
    }

    private void sendMessage(long chatId, String text) {
        try {
            SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
            botClient.execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onRegister() {
        System.out.println("TourBot registered!");
    }

    @Override
    public void onClosing() {
        System.out.println("TourBot closing!");
    }
}
