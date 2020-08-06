package ru.homyakin.gwent.telegram;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.homyakin.gwent.config.BotConfiguration;
import ru.homyakin.gwent.service.CommandsExecutor;

@Component
public class Bot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final String token;
    private final String username;
    private final CommandsExecutor commandsExecutor;
    private final ThreadPoolExecutor tasksExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    public Bot(BotConfiguration configuration,
               CommandsExecutor commandsExecutor) {
        token = configuration.getToken();
        username = configuration.getUsername();
        this.commandsExecutor = commandsExecutor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        tasksExecutor.submit(commandsExecutor.processUpdate(this, update));
    }

    public Optional<Message> sendMessage(SendMessage message) {
        try {
            return Optional.of(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message", e);
        }
        return Optional.empty();
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onClosing() {
        super.onClosing();
        this.tasksExecutor.shutdown();
    }
}
