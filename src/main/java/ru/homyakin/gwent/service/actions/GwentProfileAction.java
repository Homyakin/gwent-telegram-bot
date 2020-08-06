package ru.homyakin.gwent.service.actions;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.homyakin.gwent.models.Command;
import ru.homyakin.gwent.models.GwentProfile;
import ru.homyakin.gwent.service.HttpService;

import javax.swing.*;

@Service
public class GwentProfileAction implements Action {
    private final static Logger logger = LoggerFactory.getLogger(GwentProfileAction.class);
    private final HttpService httpService;

    public GwentProfileAction(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @NotNull
    @Override
    public Runnable fire(@NotNull AbsSender absSender, @NotNull Update update) {
        final String chatId = update.getMessage().getChatId().toString();
        if (!update.hasEditedMessage() && update.getMessage().hasText()) {
            final String userName = Command.GET_PROFILE.getTextAfterCommand(update.getMessage().getText());
            return () -> {
                final String resultMessage = getProfile(userName).map(
                        GwentProfile::toString
                ).orElse(String.format("Профиля %s не существует или он скрыт", userName));
                try {
                    absSender.execute(new SendMessage(chatId, resultMessage));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            };
        } else {
            return () -> logger.error("Пустое сообщение!");
        }
    }

    @Override
    public boolean canFire(@NotNull Message message) {
        return message.hasText() && message.getText().toLowerCase().startsWith(Command.GET_PROFILE.getValue());
    }

    public Optional<GwentProfile> getProfile(String name) {
        logger.info(String.format("Пытаемся достать профиль для юзера %s", name));
        var body = httpService.getHtmlBodyByUrl(
                String.format("https://www.playgwent.com/en/profile/%s", name)
        );
        if (body.isEmpty()) return Optional.empty();
        try {
            var doc = Jsoup.parse(body.get());
            var currentRankedSeason = doc
                    .getElementsByClass("c-statistics-table current-ranked")
                    .get(0)
                    .getElementsByTag("tbody")
                    .get(0)
                    .getElementsByTag("tr");
            var nick = getName(doc);
            var prestige = getPrestige(doc);
            var level = getLevel(doc);
            var mmr = getMmr(doc);
            var rank = getRank(doc);
            var position = getPosition(doc);
            var matches = getMatchesInSeason(currentRankedSeason.get(0));
            var wins = getTypedMatchesInSeason(currentRankedSeason.get(1));
            var loses = getTypedMatchesInSeason(currentRankedSeason.get(2));
            var draws = getTypedMatchesInSeason(currentRankedSeason.get(3));
            return Optional.of(new GwentProfile(
                    nick,
                    level,
                    prestige,
                    mmr,
                    position,
                    matches,
                    wins,
                    loses,
                    draws,
                    rank
            ));
        } catch (Exception e) {
            logger.error("Unknown error", e);
        }
        return Optional.empty();
    }

    private String getName(Document doc) {
        return doc.getElementsByClass("l-player-details__name").get(0).text();
    }

    private String getPrestige(Document doc) {
        var set = doc
                .getElementsByClass("l-player-details__prestige")
                .get(0)
                .classNames();
        set.remove("l-player-details__prestige");
        var s = (String) set.toArray()[0];
        return s.replace("l-player-details__prestige--", "");
    }

    private String getLevel(Document doc) {
        return doc.getElementsByClass("l-player-details__prestige").get(0).text();
    }

    private String getRank(Document doc) {
        return doc.getElementsByClass("l-player-details__rank").get(0).text();
    }

    private String getPosition(Document doc) {
        return doc
                .getElementsByClass("l-player-details__table-position")
                .get(0)
                .getElementsByTag("strong")
                .get(0)
                .text()
                .replace(" ", "");
    }

    private String getMmr(Document doc) {
        return doc
                .getElementsByClass("l-player-details__table-mmr")
                .get(0)
                .getElementsByTag("strong")
                .get(0)
                .text()
                .replace(" ", "");
    }

    private String getMatchesInSeason(Element element) {
        return element
                .getElementsByTag("strong")
                .get(2)
                .text()
                .replace(" matches", "");
    }

    private String getTypedMatchesInSeason(Element element) {
        return element
                .getElementsByTag("td")
                .get(1)
                .text()
                .replace(" matches", "");
    }
}
