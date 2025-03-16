package com.neyrad.bot.handler;

import com.neyrad.bot.service.MenuService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashSet;
import java.util.Set;

@Component
public class CommandHandler {
    private final MenuService menuService;
    private final Set<Long> sentMenus = new HashSet<>();

    public CommandHandler(MenuService menuService) {
        this.menuService = menuService;
    }

    public SendMessage handleCommand(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();

        if (text.equals("/start")) {
            return getStartMessage(chatId);
        }

        return null;
    }

    private SendMessage getStartMessage(Long chatId) {
        if (sentMenus.contains(chatId)) {
            return null;
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("👋 Hello! This is Expense Tracker Bot. Choose an action:");
        message.setReplyMarkup(menuService.getMainMenu());

        sentMenus.add(chatId);
        return message;
    }
}
