package com.toursearch.bot;

import com.toursearch.service.AdminNotificationService;
import com.toursearch.service.StatsService;
import com.toursearch.tour_search_api.TourDto;
import com.toursearch.tour_search_api.TourSearchController;
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
    private final StatsService statsService;
    private final AdminNotificationService notificationService;
    private final Set<Long> awaitingCountry = ConcurrentHashMap.newKeySet();

    public TourBot(TourSearchController tourSearchController, StatsService statsService,
                   AdminNotificationService notificationService) {
        this.tourSearchController = tourSearchController;
        this.statsService = statsService;
        this.notificationService = notificationService;
        this.botToken = System.getProperty("telegram.bot.token", "");
        this.botUsername = "germes_travel_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();

            if (awaitingCountry.contains(chatId)) {
                awaitingCountry.remove(chatId);
                searchTours(chatId, messageText, username);
                return;
            }

            switch (messageText) {
                case "/start" -> {
                    statsService.recordUser(chatId, username);
                    notificationService.notifyNewUser(chatId, username);
                    sendMessage(chatId, "Привет! Я бот для поиска туров. Нажми /search для поиска.");
                }
                case "/help" -> sendMessage(chatId, "Доступные команды:\n/start — начать\n/search — поиск туров\n/help — помощь");
                case "/search" -> {
                    awaitingCountry.add(chatId);
                    sendMessage(chatId, "Введите страну для поиска туров (например: Турция, Египет, ОАЭ)");
                }
                default -> sendMessage(chatId, "Не понял. Используйте /help");
            }
        }
    }

    private void searchTours(long chatId, String country, String username) {
        try {
            List<TourDto> tours = tourSearchController.search(country, null, "", 7, 2, 0).getBody();
            int resultsCount = tours != null ? tours.size() : 0;
            statsService.recordSearch(country, resultsCount);
            notificationService.notifySearch(country, resultsCount);

            if (tours == null || tours.isEmpty()) {
                sendMessage(chatId, "Туры в \"" + country + "\" не найдены. Попробуйте другую страну.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Туры в \"").append(country).append("\":\n\n");
                int count = 1;
                for (TourDto t : tours) {
                    sb.append(count++).append(". ").append(t.getHotelName()).append("\n");
                    sb.append("   ").append(t.getCity()).append(" | \u2b50").append(t.getRating()).append("\n");
                    sb.append("   ").append(t.getPrice()).append(" \u20bd\n\n");
                }
                sb.append("/search — новый поиск");
                sendMessage(chatId, sb.toString());
            }
        } catch (Exception e) {
            statsService.recordError(e.getMessage());
            notificationService.notifyError(e.getMessage());
            sendMessage(chatId, "Ошибка при поиске туров. Попробуйте позже.\n/search — новый поиск");
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
    public String getBotUsername() { return botUsername; }

    @Override
    public String getBotToken() { return botToken; }
}