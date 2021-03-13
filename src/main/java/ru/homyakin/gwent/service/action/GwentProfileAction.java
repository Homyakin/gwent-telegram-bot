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

    public Either<EitherError, CommandResponse> getProfile(Optional<String> name, Long userId) {
        return name
            .map(this::getProfileByName)
            .orElse(
                usersRepository.getProfileById(userId)
                    .flatMap(this::getProfileByName)
            );
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
            var nick = GwentProfileUtils.getName(doc);
            var prestige = GwentProfileUtils.getPrestige(doc);
            var level = GwentProfileUtils.getLevel(doc);
            var mmr = GwentProfileUtils.getMmr(doc);
            var rank = GwentProfileUtils.getRank(doc);
            var position = GwentProfileUtils.getPosition(doc);
            var currentSeason = GwentProfileUtils.getCurrentSeason(doc);
            var gwentProfile = new GwentProfile(
                nick,
                level,
                prestige,
                mmr,
                position,
                rank,
                currentSeason
            );
            var avatarLink = GwentProfileUtils.getProfileAvatarLink(doc);
            var borderLink = GwentProfileUtils.getProfileBorderLink(doc);
            var image = ImageUtils.combineAvatarAndBorder(avatarLink, borderLink);
            return Either.right(new CommandResponse(gwentProfile.toString(), image.orElse(null)));
        } catch (Exception e) {
            logger.error("Unexpected error during parsing profile", e);
            return Either.left(new ParsingError());
        }
    }
}
