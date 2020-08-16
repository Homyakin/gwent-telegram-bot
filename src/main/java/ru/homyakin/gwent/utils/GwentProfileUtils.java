package ru.homyakin.gwent.utils;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homyakin.gwent.models.CurrentSeason;
import ru.homyakin.gwent.models.FactionCards;
import ru.homyakin.gwent.models.FactionType;
import ru.homyakin.gwent.models.FactionMatches;

public class GwentProfileUtils {
    private final static Logger logger = LoggerFactory.getLogger(GwentProfileUtils.class);

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

    public static CurrentSeason getCurrentSeason(Document doc) {
        if (doc.getElementsByClass("c-statistics-table current-ranked").size() == 0) {
            return new CurrentSeason();
        }
        var currentRankedSeason = doc
            .getElementsByClass("c-statistics-table current-ranked")
            .get(0)
            .getElementsByTag("tbody")
            .get(0)
            .getElementsByTag("tr");
        var matches = getMatchesInSeason(currentRankedSeason.get(0));
        var wins = getTypedMatchesInSeason(currentRankedSeason.get(1));
        var loses = getTypedMatchesInSeason(currentRankedSeason.get(2));
        var draws = getTypedMatchesInSeason(currentRankedSeason.get(3));
        var factionMatches = new HashMap<FactionType, FactionMatches>();
        for (int i = 4; i < currentRankedSeason.size(); ++i) {
            var factionData = currentRankedSeason.get(i);
            FactionType factionType;
            if (factionData.toString().contains(FactionType.MONSTERS.getSiteName())) {
                factionType = FactionType.MONSTERS;
            } else if (factionData.toString().contains(FactionType.SKELLIGE.getSiteName())) {
                factionType = FactionType.SKELLIGE;
            } else if (factionData.toString().contains(FactionType.NILFGAARD.getSiteName())) {
                factionType = FactionType.NILFGAARD;
            } else if (factionData.toString().contains(FactionType.NORTHERN_REALMS.getSiteName())) {
                factionType = FactionType.NORTHERN_REALMS;
            } else if (factionData.toString().contains(FactionType.SCOIATAEL.getSiteName())) {
                factionType = FactionType.SCOIATAEL;
            } else if (factionData.toString().contains(FactionType.SYNDICATE.getSiteName())) {
                factionType = FactionType.SYNDICATE;
            } else {
                continue;
            }
            var factionMatchesCount = Integer.parseInt(
                factionData
                    .getElementsByTag("td")
                    .get(1)
                    .text()
                    .replace(" matches", "")
                    .replace(",", "")
            );
            factionMatches.put(
                factionType, new FactionMatches(factionType, factionMatchesCount)
            );
        }
        return new CurrentSeason(
            matches,
            wins,
            loses,
            draws,
            factionMatches
        );
    }

    public static List<FactionCards> getCardsData(Document doc) {
        var gwentCards = new ArrayList<FactionCards>();
        for (var faction : FactionType.values()) {
            var factionBar = doc.getElementsByClass(String.format("c-faction-bar--%s", faction.getSiteName())).get(0);
            var cards = Integer.parseInt(
                factionBar.getElementsByClass("stats_count").get(0).text().replace(",", "")
            );
            var totalCards = Integer.parseInt(
                factionBar.getElementsByClass("stats_overall").get(0).text().replace(",", "")
            );
            gwentCards.add(new FactionCards(faction, cards, totalCards));
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

    public static Optional<String> getProfileBorderLink(Document doc) {
        if (doc
            .getElementsByClass("l-player-details__border")
            .get(0)
            .getElementsByTag("img")
            .size() == 0
        ) {
            return Optional.empty();
        }
        return Optional.of(doc
            .getElementsByClass("l-player-details__border")
            .get(0)
            .getElementsByTag("img")
            .get(0)
            .attributes()
            .get("src"));
    }

    public static boolean isHidden(Document doc) {
        return doc.getElementsByClass("icon-private").size() != 0;
    }

    public static boolean isNotFound(Document doc) {
        return doc.getElementsByClass("icon-not-found").size() != 0;
    }

    public static String getWinsData(Document doc) {
        var allWinsTable = doc
            .getElementsByClass("c-statistics-table wins-table")
            .get(0)
            .getElementsByTag("tbody")
            .get(0)
            .getElementsByTag("tr");
        var winsData = new StringBuilder();
        for (var row : allWinsTable) {
            winsData.append(row.text()).append("\n");
        }
        return winsData.toString();
    }

    public static List<FactionMatches> getCurrentSeasonFactionWins(Document doc) {
        try {
            String script = doc.getElementsByTag("script").get(17).html();
            var pattern = Pattern.compile("profileDataCurrent.*");
            var matcher = pattern.matcher(script);
            if (matcher.find()) {
                var json = script.substring(matcher.start() + 20, matcher.end() - 1);
                var obj = new JSONObject(json);
                var winsList = new ArrayList<FactionMatches>();
                var factions = obj.getJSONArray("factions");
                for (int i = 0; i < factions.length(); ++i) {
                    var faction = FactionType.fromSiteName(factions.getJSONObject(i).getString("slug"));
                    var wins = factions.getJSONObject(i).getInt("count");
                    winsList.add(new FactionMatches(faction, wins));
                }
                return winsList;
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Error during parsing season wins", e);
            return Collections.emptyList();
        }
    }

    private static int getMatchesInSeason(Element element) {
        return Integer.parseInt(
            element
                .getElementsByTag("strong")
                .get(2)
                .text()
                .replace(" matches", "")
                .replace(",", "")
        );
    }

    private static int getTypedMatchesInSeason(Element element) {
        return Integer.parseInt(
            element
                .getElementsByTag("td")
                .get(1)
                .text()
                .replace(" matches", "")
                .replace(",", "")
        );
    }
}
