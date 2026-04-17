package com.example.bot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartCommandHandler {

    public void execute(AbsSender sender, Update update) throws Exception {
        Long chatId = update.getMessage().getChatId();
        String firstName = update.getMessage().getFrom().getFirstName();

        WebAppInfo webAppInfo = new WebAppInfo("https://blog-described-winds-astronomy.trycloudflare.com");

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("🔍 Поиск туров");
        button.setWebApp(webAppInfo);

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(new ArrayList<>(List.of(button)));

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.setKeyboard(rows);

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Привет, " + firstName + "! Нажми кнопку ниже для поиска туров:")
                .replyMarkup(keyboard)
                .build();

        sender.execute(message);
    }
}
