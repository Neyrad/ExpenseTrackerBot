package com.neyrad.bot.service;

import com.neyrad.bot.repository.ExpenseRepository;
import com.neyrad.bot.state.Expense;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatsService {
    private final ExpenseRepository expenseRepository;
    private final MenuService menuService;

    public StatsService(ExpenseRepository expenseRepository, MenuService menuService) {
        this.expenseRepository = expenseRepository;
        this.menuService = menuService;
    }

    public enum Period {
        TODAY,
        WEEK,
        MONTH
    }

    public SendMessage getStats(Long chatId, Long userId) {
        List<Expense> expenses = filterExpensesByPeriod(expenseRepository.getUserExpenses(userId), Period.TODAY);
        String text = getFormattedStats(expenses);
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(menuService.getStatsMenu());
        return message;
    }

    private String getCategoryEmoji(String category) {
        Map<String, String> emojiMap = Map.of(
                "category_food", "🍔",
                "category_transport", "🚕",
                "category_book", "📚"
        );
        return emojiMap.getOrDefault(category, "❓");
    }

    public String getFormattedStats(List<Expense> expenses) {
        Map<String, List<Expense>> groupedExpenses = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory));

        StringBuilder sb = new StringBuilder("📊 Your stats:\n\n");

        for (Map.Entry<String, List<Expense>> entry : groupedExpenses.entrySet()) {
            String category = entry.getKey();
            List<Expense> expenseList = entry.getValue();
            double total = expenseList.stream().mapToDouble(Expense::getAmount).sum();

            sb.append(getCategoryEmoji(category)).append(" ").append(category)
                    .append(": ").append(total).append(" rub\n");

            for (Expense expense : expenseList) {
                sb.append("   ├ ").append(expense.getDescription())
                        .append(" – ").append(expense.getAmount()).append(" rub\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public List<Expense> filterExpensesByPeriod(List<Expense> expenses, Period period) {
        LocalDateTime now = LocalDateTime.now();

        return expenses.stream()
                .filter(expense -> {
                    LocalDateTime expenseDate = expense.getDate();
                    return switch (period) {
                        case TODAY -> expenseDate.toLocalDate().equals(now.toLocalDate());
                        case WEEK -> expenseDate.isAfter(now.minusWeeks(1));
                        case MONTH -> expenseDate.isAfter(now.minusMonths(1));
                    };
                })
                .collect(Collectors.toList());
    }

    public void resetStats(Long userId) {
        expenseRepository.deleteByUserId(userId);
    }

}
