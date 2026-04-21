package com.toursearch.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TourBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Set<Long> awaitingCountry = ConcurrentHashMap.newKeySet();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();

            if (awaitingCountry.contains(chatId)) {
                awaitingCountry.remove(chatId);
                searchTours(chatId, messageText);
                return;
            }

            switch (messageText) {
                case "/start" -> sendMessage(chatId, "Привет! Я бот для поиска туров. Используй /search для поиска.");
                case "/help" -> sendMessage(chatId, "Доступные команды:\n/start - Запуск\n/search - Поиск туров\n/help - Помощь");
                case "/search" -> {
                    awaitingCountry.add(chatId);
                    sendMessage(chatId, "Введи страну для поиска туров (например: Турция, Египет, ОАЭ)");
                }
                default -> sendMessage(chatId, "Не понимаю. Используй /help");
            }
        }
    }

    private void searchTours(long chatId, String country) {
        try {
            String url = "http://localhost:8080/api/tours/search?country=" + country;
            String result = restTemplate.getForObject(url, String.class);
            if (result == null || result.isEmpty() || "[]".equals(result)) {
                sendMessage(chatId, "Туры в \"" + country + "\" не найдены. Попробуйте другую страну.");
            } else {
                sendMessage(chatId, "Туры в \"" + country + "\":\n\n" + formatResults(result));
            }
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при поиске туров. Попробуйте позже.\n/search - повторить поиск");
        }
    }

    private String formatResults(String json) {
        try {
            json = json.replace("[", "").replace("]", "").replace("{", "").replace("}", "");
            String[] items = json.split("\\},\\{");
            StringBuilder sb = new StringBuilder();
            int count = 1;
            for (String item : items) {
                sb.append(count++).append(". ");
                String[] fields = item.split(",");
                for (String field : fields) {
                    field = field.trim().replace("\"", "");
                    if (field.contains(":")) {
                        String[] kv = field.split(":", 2);
                        sb.append(kv[0]).append(": ").append(kv[1]).append("\n");
                    }
                }
                sb.append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return json;
        }
    }

    private void sendMessage(long chatId, String text) {
        try {
            SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}