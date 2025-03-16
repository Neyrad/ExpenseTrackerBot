package com.neyrad.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class MenuService {
    public InlineKeyboardMarkup getMainMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                createButton("➕ Add expense", "add_expense"),
                createButton("📊 Show stats", "view_stats")
        ));
        rows.add(List.of(
                createButton("⚙️ Settings", "settings"),
                createButton("ℹ️ Help", "help")
        ));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getSettingsMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                createButton("💰 Currency", "settings_currency"),
                createButton("📉 Expense limit", "settings_limit")
        ));
        rows.add(List.of(
                createButton("⬅️ Back", "main_menu")
        ));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getCategoryMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                createButton("Food", "category_food"),
                createButton("Transport", "category_transport"),
                createButton("Books", "category_book")
        ));
        rows.add(List.of(
                createButton("⬅️ Back", "main_menu")
        ));

        markup.setKeyboard(rows);
        return markup;
    }

    public InlineKeyboardMarkup getStatsMenu() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(
                createButton("Reset stats", "stats_reset")
        ));
        rows.add(List.of(
                createButton("⬅️ Back", "main_menu")
        ));

        markup.setKeyboard(rows);
        return markup;
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        return button;
    }
}
