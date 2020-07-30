package ru.homyakin.gwent;

import io.micronaut.configuration.picocli.PicocliRunner;
import javax.inject.Singleton;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import picocli.CommandLine;
import ru.homyakin.gwent.telegram.Bot;

@CommandLine.Command(name = "gwent-telegram-bot")
@Singleton
public class Application implements Runnable {

    private final Bot bot;

    public Application(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        try {
            var telegramBotsApi = new TelegramBotsApi();
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args) {
        ApiContextInitializer.init();
        PicocliRunner.run(Application.class, args);
    }
}
