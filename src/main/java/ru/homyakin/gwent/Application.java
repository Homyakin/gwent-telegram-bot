package ru.homyakin.gwent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.homyakin.gwent.telegram.Bot;

@SpringBootApplication
public class Application implements CommandLineRunner {
    private final Bot bot;

    public Application(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run(String... args) throws Exception {
        var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}
