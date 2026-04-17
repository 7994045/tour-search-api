package com.toursearch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class TourSearchBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String webAppUrl;

    public TourSearchBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.webapp-url}") String webAppUrl
    ) {
        super(botToken);
        this.botUsername = botUsername;
        this.webAppUrl = webAppUrl;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if ("/start".equals(update.getMessage().getText())) {
                sendWelcomeMessage(update.getMessage().getChatId());
            }
        }
    }

    private void sendWelcomeMessage(Long chatId) {
        WebAppInfo webAppInfo = new WebAppInfo(webAppUrl);

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("🔍 Поиск туров");
        button.setWebApp(webAppInfo);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(button));

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Привет! Нажми кнопку для поиска туров:")
                .replyMarkup(keyboard)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
