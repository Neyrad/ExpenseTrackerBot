package com.neyrad.bot.handler;

import com.neyrad.bot.repository.ExpenseRepository;
import com.neyrad.bot.service.MenuService;
import com.neyrad.bot.service.UserStateService;
import com.neyrad.bot.state.UserExpenseState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;

@Component
public class MessageHandler {
    private final UserStateService userStateService;
    private final MenuService menuService;
    private final ExpenseRepository expenseRepository;

    public MessageHandler(UserStateService userStateService, ExpenseRepository expenseRepository, MenuService menuService) {
        this.userStateService = userStateService;
        this.menuService = menuService;
        this.expenseRepository = expenseRepository;
    }

    public SendMessage handleMessage(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText().trim();
        Long userId = message.getFrom().getId();

        UserExpenseState state = userStateService.getUserState(chatId);

        if (state.getCategory() != null && state.getAmount() == null) {
            try {
                int amount = Integer.parseInt(text);
                state.setAmount(amount);
                userStateService.setUserState(chatId, state);

                SendMessage respondMessage = new SendMessage();
                respondMessage.setChatId(chatId.toString());
                respondMessage.setText("Type in expense description:");

                return respondMessage;
            } catch (NumberFormatException e) {
                return new SendMessage(chatId.toString(), "❗ Type in a correct expense sum.");
            }
        }

        if (state.getCategory() != null && state.getAmount() != null && state.getDescription() == null) {
            state.setDescription(text);
            String sql = "INSERT INTO expenses (user_id, category, amount, description, date) VALUES (?, ?, ?, ?, ?)";
            expenseRepository.executeUpdate(sql, userId, state.getCategory(), state.getAmount(), state.getDescription(), LocalDateTime.now());
            state.reset();
            userStateService.setUserState(chatId, state);

            SendMessage respondMessage = new SendMessage();
            respondMessage.setChatId(chatId.toString());
            respondMessage.setText("✅ Expense saved!");
            respondMessage.setReplyMarkup(menuService.getMainMenu());

            return respondMessage;
        }

        if (state.getCategory() == null) {
            return new SendMessage(chatId.toString(), "❗ Choose a category first.");
        }

        return new SendMessage(chatId.toString(), "❗ Unknown command 🤔");
    }
}
