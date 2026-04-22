package com.toursearch.service;

import com.toursearch.bot.AdminBot;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class AdminNotificationService {

    private final AdminBot adminBot;
    private final Long adminChatId;

    public AdminNotificationService(AdminBot adminBot) {
        this.adminBot = adminBot;
        String chatIdStr = System.getProperty("admin.chat.id", "0");
        this.adminChatId = Long.parseLong(chatIdStr);
    }

    public void notifyNewUser(long chatId, String username) {
        sendToAdmin(String.format("\u1f195 Новый пользователь!\nID: %d\nUsername: @%s",
                chatId, username != null ? username : "нет"));
    }

    public void notifyError(String error) {
        sendToAdmin(String.format("\u26a0 Ошибка:\n%s",
                error.length() > 500 ? error.substring(0, 500) : error));
    }

    public void notifySearch(String country, int results) {
        sendToAdmin(String.format("\u1f50d Поиск: %s — %d результатов", country, results));
    }

    private void sendToAdmin(String text) {
        if (adminChatId == 0) return;
        try {
            adminBot.execute(SendMessage.builder().chatId(adminChatId).text(text).build());
        } catch (TelegramApiException e) {
            System.err.println("Admin notification error: " + e.getMessage());
        }
    }
}