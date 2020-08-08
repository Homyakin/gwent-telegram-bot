package ru.homyakin.gwent.service.action;

import io.vavr.control.Either;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.GwentProfile;
import ru.homyakin.gwent.models.exceptions.EitherError;
import ru.homyakin.gwent.models.exceptions.ParsingError;
import ru.homyakin.gwent.models.exceptions.ProfileIsHidden;
import ru.homyakin.gwent.models.exceptions.ProfileNotFound;
import ru.homyakin.gwent.service.HttpService;
import ru.homyakin.gwent.utils.GwentProfileUtils;

@Service
public class GwentProfileAction {
    private final static Logger logger = LoggerFactory.getLogger(GwentProfileAction.class);
    private final HttpService httpService;

    public GwentProfileAction(HttpService httpService) {
        this.httpService = httpService;
    }

    public Either<EitherError, CommandResponse> getProfile(String name) {
        var url = String.format("https://www.playgwent.com/en/profile/%s", name);
        var body = httpService.getHtmlBodyByUrl(url);
        if (body.isLeft()) {
            return Either.left(body.getLeft());
        }
        try {
            var doc = Jsoup.parse(body.get());
            if (GwentProfileUtils.isHidden(doc)) {
                return Either.left(new ProfileIsHidden(name));
            }
            if (GwentProfileUtils.isNotFound(doc)) {
                return Either.left(new ProfileNotFound(name));
            }
            var currentRankedSeason = doc
                .getElementsByClass("c-statistics-table current-ranked")
                .get(0)
                .getElementsByTag("tbody")
                .get(0)
                .getElementsByTag("tr");
            var nick = GwentProfileUtils.getName(doc);
            var prestige = GwentProfileUtils.getPrestige(doc);
            var level = GwentProfileUtils.getLevel(doc);
            var mmr = GwentProfileUtils.getMmr(doc);
            var rank = GwentProfileUtils.getRank(doc);
            var position = GwentProfileUtils.getPosition(doc);
            var matches = GwentProfileUtils.getMatchesInSeason(currentRankedSeason.get(0));
            var wins = GwentProfileUtils.getTypedMatchesInSeason(currentRankedSeason.get(1));
            var loses = GwentProfileUtils.getTypedMatchesInSeason(currentRankedSeason.get(2));
            var draws = GwentProfileUtils.getTypedMatchesInSeason(currentRankedSeason.get(3));
            var gwentProfile = new GwentProfile(
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
            ).toString();
            return Either.right(new CommandResponse(gwentProfile, GwentProfileUtils.getProfileAvatarLink(doc)));
        } catch (Exception e) {
            logger.error("Unexpected error during parsing", e);
            return Either.left(new ParsingError());
        }
    }
}
