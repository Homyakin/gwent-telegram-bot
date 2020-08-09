package ru.homyakin.gwent.service.action;

import io.vavr.control.Either;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.database.UsersRepository;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.ParsingError;
import ru.homyakin.gwent.models.errors.ProfileIsHidden;
import ru.homyakin.gwent.models.errors.ProfileNotFound;
import ru.homyakin.gwent.service.HttpService;
import ru.homyakin.gwent.utils.GwentProfileUtils;

@Service
public class AllWinsAction {
    private final static Logger logger = LoggerFactory.getLogger(AllWinsAction.class);
    private final UsersRepository usersRepository;
    private final HttpService httpService;

    public AllWinsAction(
        UsersRepository usersRepository,
        HttpService httpService
    ) {
        this.usersRepository = usersRepository;
        this.httpService = httpService;
    }

    public Either<EitherError, CommandResponse> getAllWins(Optional<String> name, int userId) {
        if (name.isEmpty()) {
            var profile = usersRepository.getProfileById(userId);
            if (profile.isRight()) {
                return getAllWinsByName(profile.get());
            } else {
                return Either.left(profile.getLeft());
            }
        }
        return getAllWinsByName(name.get());
    }

    private Either<EitherError, CommandResponse> getAllWinsByName(String name) {
        var body = httpService.getHtmlBodyByUrl(String.format("https://www.playgwent.com/ru/profile/%s", name));
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

            return Either.right(new CommandResponse(
                GwentProfileUtils.getName(doc) + "\n" + GwentProfileUtils.getWinsData(doc))
            );
        } catch (Exception e) {
            logger.error("Unexpected error during parsing", e);
            return Either.left(new ParsingError());
        }
    }
}
