package ru.homyakin.gwent.utils;


import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.homyakin.gwent.models.FactionCardsData;
import ru.homyakin.gwent.models.FactionType;

public class GwentProfileUtils {
    public static String getName(Document doc) {
        return doc.getElementsByClass("l-player-details__name").get(0).text();
    }

    public static String getPrestige(Document doc) {
        var set = doc
            .getElementsByClass("l-player-details__prestige")
            .get(0)
            .classNames();
        set.remove("l-player-details__prestige");
        var s = (String) set.toArray()[0];
        return s.replace("l-player-details__prestige--", "");
    }

    public static String getLevel(Document doc) {
        return doc.getElementsByClass("l-player-details__prestige").get(0).text();
    }

    public static String getRank(Document doc) {
        return doc.getElementsByClass("l-player-details__rank").get(0).text();
    }

    public static String getPosition(Document doc) {
        return doc
            .getElementsByClass("l-player-details__table-position")
            .get(0)
            .getElementsByTag("strong")
            .get(0)
            .text()
            .replace(" ", "");
    }

    public static String getMmr(Document doc) {
        return doc
            .getElementsByClass("l-player-details__table-mmr")
            .get(0)
            .getElementsByTag("strong")
            .get(0)
            .text()
            .replace(" ", "");
    }

    public static String getMatchesInSeason(Element element) {
        return element
            .getElementsByTag("strong")
            .get(2)
            .text()
            .replace(" matches", "");
    }

    public static String getTypedMatchesInSeason(Element element) {
        return element
            .getElementsByTag("td")
            .get(1)
            .text()
            .replace(" matches", "");
    }

    public static List<FactionCardsData> getCardsData(Document doc) {
        var gwentCards = new ArrayList<FactionCardsData>();
        for (var faction : FactionType.values()) {
            var factionBar = doc.getElementsByClass(String.format("c-faction-bar--%s", faction.getSiteName())).get(0);
            var cards = Integer.parseInt(
                factionBar.getElementsByClass("stats_count").get(0).text().replace(",", "")
            );
            var totalCards = Integer.parseInt(
                factionBar.getElementsByClass("stats_overall").get(0).text().replace(",", "")
            );
            gwentCards.add(new FactionCardsData(faction, cards, totalCards));
        }
        return gwentCards;
    }

    public static String getProfileAvatarLink(Document doc) {
        return doc
            .getElementsByClass("l-player-details__avatar")
            .get(0)
            .getElementsByTag("img")
            .get(0)
            .attributes()
            .get("src");
    }

    public static boolean isHidden(Document doc) {
        return doc.getElementsByClass("icon-private").size() != 0;
    }

    public static boolean isNotFound(Document doc) {
        return doc.getElementsByClass("icon-not-found").size() != 0;
    }
}
