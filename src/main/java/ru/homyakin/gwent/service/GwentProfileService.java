package ru.homyakin.gwent.service;

import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.GwentProfile;

@Service
public class GwentProfileService {
    private final static Logger logger = LoggerFactory.getLogger(GwentProfileService.class);
    private final HttpService httpService;

    public GwentProfileService(HttpService httpService) {
        this.httpService = httpService;
    }

    public Optional<GwentProfile> getProfile(String name) {
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
                name,
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
        return element.getElementsByTag("strong").get(2).text().replace(" matches", "");
    }

    private String getTypedMatchesInSeason(Element element) {
        return element.getElementsByTag("td").get(1).text().replace(" matches", "");
    }
}
