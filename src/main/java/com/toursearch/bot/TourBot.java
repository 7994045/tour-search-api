package com.toursearch.bot;

import com.toursearch.service.StatsService;
import com.toursearch.tour_search_api.TourDto;
import com.toursearch.tour_search_api.TourSearchController;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class TourBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername = "germes_travel_bot";
    private final TourSearchController tourSearchController;

    @Autowired
    private StatsService statsService;

    private final Set<Long> awaitingCountry = ConcurrentHashMap.newKeySet();

    public TourBot(TourSearchController tourSearchController, String botToken) {
        this.tourSearchController = tourSearchController;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String messageText = update.getMessage().getText().trim();
        long chatId = update.getMessage().getChatId();

        if (awaitingCountry.contains(chatId)) {
            awaitingCountry.remove(chatId);
            searchTours(chatId, messageText);
            return;
        }

        switch (messageText) {
            case "/start" -> sendMessage(chatId, "\u041f\u0440\u0438\u0432\u0435\u0442! \u042f \u0431\u043e\u0442 \u0434\u043b\u044f \u043f\u043e\u0438\u0441\u043a\u0430 \u0442\u0443\u0440\u043e\u0432. \u041d\u0430\u0436\u043c\u0438 /search \u0434\u043b\u044f \u043f\u043e\u0438\u0441\u043a\u0430.");
            case "/help" -> sendMessage(chatId, "\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b:\n/start \u2014 \u043d\u0430\u0447\u0430\u0442\u044c\n/search \u2014 \u043f\u043e\u0438\u0441\u043a \u0442\u0443\u0440\u043e\u0432\n/help \u2014 \u043f\u043e\u043c\u043e\u0449\u044c");
            case "/search" -> {
                awaitingCountry.add(chatId);
                sendMessage(chatId, "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0441\u0442\u0440\u0430\u043d\u0443 \u0434\u043b\u044f \u043f\u043e\u0438\u0441\u043a\u0430 \u0442\u0443\u0440\u043e\u0432 (\u043d\u0430\u043f\u0440\u0438\u043c\u0435\u0440: \u0422\u0443\u0440\u0446\u0438\u044f, \u0415\u0433\u0438\u043f\u0435\u0442, \u041e\u0410\u042d)");
            }
            default -> sendMessage(chatId, "\u041d\u0435 \u043f\u043e\u043d\u044f\u043b. /help \u2014 \u0441\u043f\u0438\u0441\u043e\u043a \u043a\u043e\u043c\u0430\u043d\u0434");
        }
    }

    private void searchTours(long chatId, String country) {
        try {
            List<TourDto> tours = tourSearchController.search(country, null, "", 7, 2, 0).getBody();
            if (statsService != null) statsService.recordSearch(country, null);
            if (tours == null || tours.isEmpty()) {
                sendMessage(chatId, "\u0422\u0443\u0440\u044b \u0432 \"" + country + "\" \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u044b.\n/search \u2014 \u043d\u043e\u0432\u044b\u0439 \u043f\u043e\u0438\u0441\u043a");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("\u0422\u0443\u0440\u044b \u0432 \"" + country + "\":\n\n");
                int count = 1;
                for (TourDto t : tours) {
                    sb.append(count++).append(". ").append(t.getHotelName()).append("\n");
                    sb.append("   ").append(t.getCity()).append(" | \u2b50").append(t.getRating()).append("\n");
                    sb.append("   ").append(t.getPrice()).append(" \u20bd\n\n");
                }
                sb.append("/search \u2014 \u043d\u043e\u0432\u044b\u0439 \u043f\u043e\u0438\u0441\u043a");
                sendMessage(chatId, sb.toString());
            }
        } catch (Exception e) {
            if (statsService != null) statsService.recordError(e.getMessage());
            sendMessage(chatId, "\u041e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u043f\u043e\u0438\u0441\u043a\u0435. /search \u2014 \u043f\u043e\u0432\u0442\u043e\u0440\u0438\u0442\u044c");
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