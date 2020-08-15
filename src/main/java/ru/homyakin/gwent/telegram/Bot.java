package ru.homyakin.gwent.telegram;

import com.vdurmont.emoji.EmojiParser;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.homyakin.gwent.config.BotConfiguration;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.UserMessage;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.UnknownCommand;
import ru.homyakin.gwent.service.CommandService;

@Component
public class Bot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final String token;
    private final String username;
    private final CommandService commandService;

    public Bot(BotConfiguration configuration, CommandService commandService) {
        token = configuration.getToken();
        username = configuration.getUsername();
        this.commandService = commandService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            var message = update.getMessage();
            if (message.isCommand()) {
                logger.info(
                    "New request {} from {} in {}",
                    message.getText(),
                    message.getFrom(),
                    message.getChat()
                );
                var response = commandService
                    .executeCommand(
                        new UserMessage(
                            message.getText(),
                            message.getFrom().getId(),
                            message.isUserMessage()
                        )
                    );
                var text = response
                    .map(CommandResponse::getText)
                    .map(EmojiParser::parseToUnicode)
                    .getOrElseGet(EitherError::getMessage);
                if (response.isRight() && response.get().getImageLink().isPresent()) {
                    var imageStream = response.get().getImageLink().get();
                    try {
                        var sendPhoto = new SendPhoto()
                            .setPhoto(imageStream.toString(), imageStream)
                            .setCaption(text)
                            .setChatId(message.getChatId());
                        sendMessage(sendPhoto);
                    } catch (Exception e) {
                        logger.error("Error during sending photo {}", imageStream, e);
                        sendTextMessage(text, message.getChatId());
                    }
                } else if (response.isRight() || response.isLeft() && !(response.getLeft() instanceof UnknownCommand)) {
                    sendTextMessage(text, message.getChatId());
                }
            }
        }
    }

    public Optional<Message> sendTextMessage(String text, Long chatId) {
        var message = new SendMessage()
            .setChatId(chatId)
            .setText(text);
        return sendMessage(message);
    }

    public Optional<Message> sendMessage(SendMessage message) {
        try {
            return Optional.of(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message", e);
        }
        return Optional.empty();
    }

    public Optional<Message> sendMessage(SendPhoto message) {
        try {
            return Optional.of(execute(message));
        } catch (TelegramApiException e) {
            logger.error("Something went wrong during sending message with photo", e);
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
}
