package ru.homyakin.gwent.service.action;

import io.vavr.control.Either;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.database.UsersRepository;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.FactionMatches;
import ru.homyakin.gwent.models.FactionType;
import ru.homyakin.gwent.models.errors.CurrentSeasonNotPresent;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.ParsingError;
import ru.homyakin.gwent.models.errors.ProfileIsHidden;
import ru.homyakin.gwent.models.errors.ProfileNotFound;
import ru.homyakin.gwent.service.HttpService;
import ru.homyakin.gwent.utils.GwentProfileUtils;

@Service
public class CurrentSeasonAction {
    private final static Logger logger = LoggerFactory.getLogger(GwentCardsAction.class);
    private final HttpService httpService;
    private final UsersRepository usersRepository;

    public CurrentSeasonAction(
        HttpService httpService,
        UsersRepository usersRepository
    ) {
        this.usersRepository = usersRepository;
        this.httpService = httpService;
    }

    public Either<EitherError, CommandResponse> getCurrentSeason(Optional<String> name, int userId) {
        try {
            return name
                .map(this::getCurrentSeasonByName)
                .orElse(
                    usersRepository.getProfileById(userId)
                        .flatMap(this::getCurrentSeasonByName)
                );
        } catch (Exception e) {
            logger.error("Unknown exception during parsing current season");
            return Either.left(new ParsingError());
        }
    }

    public Either<EitherError, CommandResponse> getCurrentSeasonByName(String name) {
        var body = httpService.getHtmlBodyByUrl(String.format("https://www.playgwent.com/en/profile/%s", name));
        if (body.isLeft()) {
            return Either.left(body.getLeft());
        }
        var doc = Jsoup.parse(body.get());
        if (GwentProfileUtils.isHidden(doc)) {
            return Either.left(new ProfileIsHidden(name));
        }
        if (GwentProfileUtils.isNotFound(doc)) {
            return Either.left(new ProfileNotFound(name));
        }
        var winsList = GwentProfileUtils.getCurrentSeasonFactionWins(doc);
        var currentSeason = GwentProfileUtils.getCurrentSeason(doc);
        var username = GwentProfileUtils.getName(doc);
        if (currentSeason.getFactionMatches().isEmpty()) {
            return Either.left(new CurrentSeasonNotPresent(username));
        }
        var response = String.format(
            "%s:\n" +
                "%s" +
                "%s",
            username,
            combineFactionMatchesAndWins(currentSeason.getFactionMatches().get(), winsList),
            currentSeason.toString()
        );
        return Either.right(new CommandResponse(response));
    }

    private String combineFactionMatchesAndWins(
        Map<FactionType, FactionMatches> factionMatches,
        List<FactionMatches> factionWins
    ) {
        var stringBuilder = new StringBuilder();
        for (var faction: factionWins) {
            var matches = factionMatches.get(faction.getFactionType()).getMatches();
            var winrate =  matches == 0 ? 0d : (double) faction.getMatches() / matches * 100;
            stringBuilder
                .append(String.format(
                    "%s: %d / %d (%.2f%%)",
                    faction.getFactionType().getRusName(),
                    faction.getMatches(),
                    matches,
                    winrate
                ))
                .append("\n");
        }
        return stringBuilder.toString();
    }
}
