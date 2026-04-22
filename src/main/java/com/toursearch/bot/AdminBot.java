package com.toursearch.bot;

import com.toursearch.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;

@Component
public class AdminBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final Long adminChatId;
    private final StatsService statsService;

    public AdminBot(StatsService statsService) {
        this.statsService = statsService;
        this.botToken = System.getProperty("admin.bot.token", System.getenv("ADMIN_BOT_TOKEN"));
        this.botUsername = System.getProperty("admin.bot.username", "GT_main_bot");
        String chatIdStr = System.getProperty("admin.chat.id", System.getenv("ADMIN_CHAT_ID"));
        this.adminChatId = (chatIdStr != null && !chatIdStr.isEmpty()) ? Long.parseLong(chatIdStr) : 0L;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        Message message = update.getMessage();
        long chatId = message.getChatId();
        String text = message.getText().trim();

        if ("/start".equals(text)) {
            sendMessage(chatId, "\u2714\ufe0f Admin Bot Germes Travel\n\u0412\u0430\u0448 chat_id: " + chatId + "\n\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0435\u0433\u043e \u0432 ADMIN_CHAT_ID");
            return;
        }

        if (adminChatId == 0 || chatId != adminChatId) return;

        switch (text) {
            case "/stats":
                sendStats(chatId);
                break;
            case "/searches":
                sendRecentSearches(chatId);
                break;
            case "/errors":
                sendRecentErrors(chatId);
                break;
            case "/health":
                sendHealth(chatId);
                break;
            default:
                sendMessage(chatId, "\u041a\u043e\u043c\u0430\u043d\u0434\u044b:\n/stats \u2014 \u0441\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430\n/searches \u2014 \u043f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0435 \u043f\u043e\u0438\u0441\u043a\u0438\n/errors \u2014 \u043e\u0448\u0438\u0431\u043a\u0438\n/health \u2014 \u0441\u0442\u0430\u0442\u0443\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430");
        }
    }

    private void sendStats(long chatId) {
        String text = String.format("\ud83d\udcca \u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430\n\n\u041f\u043e\u0438\u0441\u043a\u043e\u0432: %d\n\u041e\u0448\u0438\u0431\u043e\u043a: %d",
            statsService.getTotalSearches(), statsService.getTotalErrors());
        sendMessage(chatId, text);
    }

    private void sendRecentSearches(long chatId) {
        StringBuilder sb = new StringBuilder("\ud83d\udd0d \u041f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0435 \u043f\u043e\u0438\u0441\u043a\u0438:\n\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        for (StatsService.SearchRecord r : statsService.getRecentSearches()) {
            sb.append("\u2022 ").append(r.country)
              .append(r.city != null ? ", " + r.city : "")
              .append(" \u2014 ").append(r.time.format(fmt)).append("\n");
        }
        if (statsService.getRecentSearches().isEmpty()) sb.append("\u041f\u043e\u043a\u0430 \u043d\u0435\u0442 \u043f\u043e\u0438\u0441\u043a\u043e\u0432");
        sendMessage(chatId, sb.toString());
    }

    private void sendRecentErrors(long chatId) {
        StringBuilder sb = new StringBuilder("\u274c \u041f\u043e\u0441\u043b\u0435\u0434\u043d\u0438\u0435 \u043e\u0448\u0438\u0431\u043a\u0438:\n\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM HH:mm");
        for (StatsService.ErrorRecord r : statsService.getRecentErrors()) {
            sb.append("\u2022 ").append(r.message).append(" \u2014 ").append(r.time.format(fmt)).append("\n");
        }
        if (statsService.getRecentErrors().isEmpty()) sb.append("\u041e\u0448\u0438\u0431\u043e\u043a \u043d\u0435\u0442! \ud83c\udf89");
        sendMessage(chatId, sb.toString());
    }

    private void sendHealth(long chatId) {
        Runtime runtime = Runtime.getRuntime();
        long totalMem = runtime.totalMemory() / 1024 / 1024;
        long freeMem = runtime.freeMemory() / 1024 / 1024;
        long usedMem = totalMem - freeMem;
        String text = String.format("\ud83d\udc9a \u0421\u0442\u0430\u0442\u0443\u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430\n\n\u041f\u0430\u043c\u044f\u0442\u044c: %d/%d \u041c\u0411\n\u0421\u0442\u0430\u0442\u0443\u0441: OK \u2705", usedMem, totalMem);
        sendMessage(chatId, text);
    }

    public void notifyAdmin(String message) {
        if (adminChatId != 0) sendMessage(adminChatId, message);
    }

    private void sendMessage(long chatId, String text) {
        try {
            SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
            execute(msg);
        } catch (TelegramApiException e) {
            System.err.println("Error sending admin message: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() { return botUsername; }
    @Override
    public String getBotToken() { return botToken; }
}