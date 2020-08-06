package ru.homyakin.gwent.service.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.FactionType;
import ru.homyakin.gwent.models.FactionCardsData;
import ru.homyakin.gwent.service.HttpService;
import java.lang.Exception;

@Service
public class GwentCardsAction {
    private final static Logger logger = LoggerFactory.getLogger(GwentCardsAction.class);
    private final HttpService httpService;

    public GwentCardsAction(HttpService httpService) {
        this.httpService = httpService;
    }

    public Optional<String> getCards(String name) {
        var body = httpService.getHtmlBodyByUrl(String.format("https://www.playgwent.com/en/profile/%s", name));
        if (body.isEmpty()) return Optional.empty();
        try {
            var doc = Jsoup.parse(body.get());
            return Optional.of(name + "\n" + parseHtml(doc));
        }catch (Exception e) {
            logger.error("Unexpected error");
            return Optional.empty();
        }
    }

    private String parseHtml(Document doc) {
        var gwentCards = new ArrayList<FactionCardsData>();
        for (var faction: FactionType.values()) {
            var factionBar = doc.getElementsByClass(String.format("c-faction-bar--%s", faction.getSiteName())).get(0);
            var cards = Integer.parseInt(
                factionBar.getElementsByClass("stats_count").get(0).text().replace(",", "")
            );
            var totalCards = Integer.parseInt(
                factionBar.getElementsByClass("stats_overall").get(0).text().replace(",", "")
            );
            gwentCards.add(new FactionCardsData(faction, cards, totalCards));
        }
        return convertAllFactionCardsToString(gwentCards);
    }

    private String convertAllFactionCardsToString(List<FactionCardsData> list) {
        StringBuilder response = new StringBuilder();
        for (var factionCards : list) {
            response.append(factionCards.toString()).append("\n");
        }
        return response.toString();
    }
}
