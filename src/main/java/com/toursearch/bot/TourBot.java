package com.toursearch.bot;

import com.toursearch.tour_search_api.TourDto;
import com.toursearch.tour_search_api.TourSearchController;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TourBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final TourSearchController tourSearchController;
    private final Set<Long> awaitingCountry = ConcurrentHashMap.newKeySet();

    public TourBot(TourSearchController tourSearchController) {
        this.tourSearchController = tourSearchController;
        this.botToken = System.getProperty("telegram.bot.token", "");
        this.botUsername = "germes_travel_bot";
    }

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
            List<TourDto> tours = tourSearchController.search(country, null, "", 7, 2, 0).getBody();
            if (tours == null || tours.isEmpty()) {
                sendMessage(chatId, "Туры в \"" + country + "\" не найдены. Попробуйте другую страну.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Туры в \"").append(country).append("\":\n\n");
                int count = 1;
                for (TourDto t : tours) {
                    sb.append(count++).append(". ").append(t.getHotelName()).append("\n");
                    sb.append("   ").append(t.getCity()).append(" | ⭐").append(t.getRating()).append("\n");
                    sb.append("   ").append(t.getPrice()).append(" ₽\n\n");
                }
                sb.append("/search - новый поиск");
                sendMessage(chatId, sb.toString());
            }
        } catch (Exception e) {
            sendMessage(chatId, "Ошибка при поиске туров. Попробуйте позже.\n/search - повторить поиск");
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