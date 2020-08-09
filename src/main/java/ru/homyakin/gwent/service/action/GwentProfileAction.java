package ru.homyakin.gwent.service.action;

import io.vavr.control.Either;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.database.UsersRepository;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.GwentProfile;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.ParsingError;
import ru.homyakin.gwent.models.errors.ProfileIsHidden;
import ru.homyakin.gwent.models.errors.ProfileNotFound;
import ru.homyakin.gwent.service.HttpService;
import ru.homyakin.gwent.utils.GwentProfileUtils;
import ru.homyakin.gwent.utils.ImageUtils;

@Service
public class GwentProfileAction {
    private final static Logger logger = LoggerFactory.getLogger(GwentProfileAction.class);
    private final HttpService httpService;
    private final UsersRepository usersRepository;

    public GwentProfileAction(
        HttpService httpService,
        UsersRepository usersRepository
    ) {
        this.httpService = httpService;
        this.usersRepository = usersRepository;
    }

    public Either<EitherError, CommandResponse> getProfile(Optional<String> name, int userId) {
        if (name.isEmpty()) {
            var profile = usersRepository.getProfileById(userId);
            if (profile.isRight()) {
                return getProfileByName(profile.get());
            } else {
                return Either.left(profile.getLeft());
            }
        }
        return getProfileByName(name.get());
    }

    public Either<EitherError, CommandResponse> getProfileByName(String name) {
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
            var avatarLink = GwentProfileUtils.getProfileAvatarLink(doc);
            var borderLink = GwentProfileUtils.getProfileBorderLink(doc);
            var image = ImageUtils.combineAvatarAndBorder(avatarLink, borderLink);
            return Either.right(new CommandResponse(gwentProfile, image.orElse(null)));
        } catch (Exception e) {
            logger.error("Unexpected error during parsing", e);
            return Either.left(new ParsingError());
        }
    }
}
