package ru.homyakin.gwent.telegram;

import com.vdurmont.emoji.EmojiParser;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.homyakin.gwent.config.BotConfiguration;
import ru.homyakin.gwent.models.exceptions.EitherError;
import ru.homyakin.gwent.models.exceptions.UnknownCommand;
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
            if (update.getMessage().isCommand()) {
                logger.info(
                    "New request {} from {} in {}",
                    update.getMessage().getText(),
                    update.getMessage().getFrom(),
                    update.getMessage().getChat()
                );
                var response = commandService.executeCommand(update.getMessage().getText());
                if ( response.isRight() || response.isLeft() &&
                    (!(response.getLeft() instanceof UnknownCommand) || update.getMessage().isUserMessage())
                ) {
                    var text = response.map(EmojiParser::parseToUnicode).getOrElseGet(EitherError::getMessage);
                    var message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(text);
                    sendMessage(message);
                }
            }
        }
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
}
