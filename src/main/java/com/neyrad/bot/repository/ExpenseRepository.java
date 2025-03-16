package com.neyrad.bot.repository;

import com.neyrad.bot.state.Expense;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ExpenseRepository {
    private final JdbcTemplate jdbcTemplate;

    public ExpenseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void executeUpdate(String sql, Object... params) {
        jdbcTemplate.update(sql, params);
    }

    public List<Expense> getUserExpenses(long userId) {
        String sql = "SELECT category, amount, description, date FROM expenses WHERE user_id = ?";

        return jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) -> {
            String category = rs.getString("category");
            double amount = rs.getDouble("amount");
            String description = rs.getString("description");
            LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();

            return new Expense(category, amount, description, date, userId);
        });
    }

    public void deleteByUserId(long userId) {
        String sql = "DELETE FROM expenses WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }
}
