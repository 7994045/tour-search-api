package com.toursearch.bot;

import com.toursearch.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AdminBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername = "GT_main_bot";
    private final Long adminChatId;
    private final StatsService statsService;

    public AdminBot(StatsService statsService) {
        this.statsService = statsService;
        this.botToken = System.getProperty("admin.bot.token", "");
        String chatIdStr = System.getProperty("admin.chat.id", "0");
        this.adminChatId = Long.parseLong(chatIdStr);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText().trim();

        if (adminChatId == 0) {
            sendMessage(chatId, "\u1f511 Ваш Chat ID: " + chatId + "\n\nУстановите:\nexport ADMIN_CHAT_ID=" + chatId);
            return;
        }

        if (chatId != adminChatId) {
            sendMessage(chatId, "\u26d4 Доступ запрещён.");
            return;
        }

        switch (messageText) {
            case "/start", "/help" -> sendMessage(chatId, """
                \u1f6e0 Админ-панель Germes Travel\n\n/stats — общая статистика\n/users — регистрации\n/searches — поиски\n/errors — последние ошибки\n/health — статус сервера""");
            case "/stats" -> sendMessage(chatId, statsService.getFullStats());
            case "/users" -> sendMessage(chatId, statsService.getUsersStats());
            case "/searches" -> sendMessage(chatId, statsService.getSearchesStats());
            case "/errors" -> sendMessage(chatId, statsService.getErrorsStats());
            case "/health" -> sendHealth(chatId);
            default -> sendMessage(chatId, "Неизвестная команда. /help — список команд");
        }
    }

    private void sendHealth(long chatId) {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long max = runtime.maxMemory() / 1024 / 1024;

        sendMessage(chatId, String.format("""
            \u1f3e5 Статус сервера:\n\u2705 Бэкенд: работает\n\u1f4be Память: %d / %d МБ\n\u1f50d Поисков: %d\n\u274c Ошибок: %d""",
            used, max, statsService.getTotalSearches(), statsService.getTotalErrors()));
    }

    private void sendMessage(long chatId, String text) {
        try {
            execute(SendMessage.builder().chatId(chatId).text(text).build());
        } catch (TelegramApiException e) {
            System.err.println("Admin bot error: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() { return botUsername; }

    @Override
    public String getBotToken() { return botToken; }
}