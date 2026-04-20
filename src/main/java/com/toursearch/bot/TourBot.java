package com.toursearch.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpBotSessionProvider;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.meta.generics.SessionIterator;

@Component
public class TourBot implements LongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void onUpdatesReceived(SessionIterator sessionIterator) {
        while (sessionIterator.hasNext()) {
            Update update = sessionIterator.next();
            
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                
                String response = processMessage(messageText);
                sendMessage(chatId, response);
            }
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
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(text);
            execute(message);
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
