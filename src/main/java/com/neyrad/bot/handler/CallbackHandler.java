package com.neyrad.bot.handler;

import com.neyrad.bot.service.MenuService;
import com.neyrad.bot.service.StatsService;
import com.neyrad.bot.service.UserStateService;
import com.neyrad.bot.state.UserExpenseState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.IOException;

@Component
public class CallbackHandler {
    private final MenuService menuService;
    private final StatsService statsService;
    private final UserStateService userStateService;

    public CallbackHandler(MenuService menuService, StatsService statsService, UserStateService userStateService) {
        this.menuService = menuService;
        this.statsService = statsService;
        this.userStateService = userStateService;
    }

    public EditMessageText handleCallback(CallbackQuery query) throws IOException {
        String data = query.getData();
        Long chatId = query.getMessage().getChatId();
        Integer messageId = query.getMessage().getMessageId();
        Long userId = query.getFrom().getId();

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId.toString());
        editMessage.setMessageId(messageId);
        editMessage.setReplyMarkup(query.getMessage().getReplyMarkup());

        String newText = processCallback(data, chatId, userId, editMessage);

        if (newText.equals(query.getMessage().getText())) {
            return null;
        }

        editMessage.setText(newText);
        return editMessage;
    }

    private String processCallback(String data, Long chatId, Long userId, EditMessageText editMessage) throws IOException {
        UserExpenseState state = userStateService.getUserState(chatId);

        switch (data) {
            case "add_expense" -> {
                return handleCategory(editMessage);
            }
            case "view_stats" -> {
                return handleStats(editMessage, userId);
            }
            case "stats_reset" -> {
                statsService.resetStats(userId);
                return "📊 Your stats:";
            }
            case "settings" -> {
                return handleSettings(editMessage);
            }
            case "main_menu" -> {
                return handleMainMenu(editMessage);
            }
            case "category_food", "category_transport", "category_book" -> {
                state.setCategory(data);
                userStateService.setUserState(chatId, state);
                editMessage.setReplyMarkup(null);
                return "Type in expense sum:";
            }
            default -> {
                return "Unknown action.";
            }
        }
    }

    private String handleSettings(EditMessageText editMessage) {
        editMessage.setReplyMarkup(menuService.getSettingsMenu());
        return "⚙️ Settings:";
    }

    private String handleMainMenu(EditMessageText editMessage) {
        editMessage.setReplyMarkup(menuService.getMainMenu());
        return "🔹 Main menu. Choose an action:";
    }

    private String handleCategory(EditMessageText editMessage) {
        editMessage.setReplyMarkup(menuService.getCategoryMenu());
        return "Choose a category:";
    }

    private String handleStats(EditMessageText editMessage, Long userId) {
        editMessage.setReplyMarkup(menuService.getStatsMenu());
        return statsService.getStats(Long.parseLong(editMessage.getChatId()), userId).getText();
    }

}

