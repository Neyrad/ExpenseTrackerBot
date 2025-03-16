package com.neyrad.bot;

import com.neyrad.bot.handler.CallbackHandler;
import com.neyrad.bot.handler.CommandHandler;
import com.neyrad.bot.handler.MessageHandler; // Імпортуємо MessageHandler
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final CallbackHandler callbackHandler;
    private final CommandHandler commandHandler;
    private final MessageHandler messageHandler;

    private final Map<Long, Integer> lastMessageId = new ConcurrentHashMap<>();

    public TelegramBot(CallbackHandler callbackHandler, CommandHandler commandHandler, MessageHandler messageHandler) {
        this.callbackHandler = callbackHandler;
        this.commandHandler = commandHandler;
        this.messageHandler = messageHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            EditMessageText editMessage = null;
            try {
                editMessage = callbackHandler.handleCallback(update.getCallbackQuery());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (editMessage != null) {
                executeMessage(editMessage);
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();

            if (lastMessageId.containsKey(chatId)) {
                deleteMessage(chatId, lastMessageId.get(chatId));
                lastMessageId.remove(chatId);
            }

            SendMessage message = commandHandler.handleCommand(update.getMessage());
            if (message == null) {
                message = messageHandler.handleMessage(update.getMessage());
            }

            if (message != null) {
                try {
                    Message sentMessage = execute(message);
                    lastMessageId.put(chatId, sentMessage.getMessageId());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void executeMessage(BotApiMethod<?> message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            execute(new DeleteMessage(chatId.toString(), messageId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            System.out.println("The bot is started!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "ExpenseTrackerBot";
    }

    @Override
    public String getBotToken() {
        return "7173753848:AAETRHrGoIeBpzNAaJJbiv6cyX0MqOrwLNI";
    }
}
