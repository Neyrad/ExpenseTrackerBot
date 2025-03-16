package com.neyrad.bot.service;

import com.neyrad.bot.state.UserExpenseState;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserStateService {
    private final Map<Long, UserExpenseState> userStates = new HashMap<>();

    public UserExpenseState getUserState(Long userId) {
        return userStates.getOrDefault(userId, new UserExpenseState());
    }

    public void setUserState(Long userId, UserExpenseState state) {
        userStates.put(userId, state);
    }

    public void clearUserState(Long userId) {
        userStates.remove(userId);
    }
}

