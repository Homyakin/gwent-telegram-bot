package ru.homyakin.gwent.service;

import io.vavr.control.Either;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.database.UsersRepository;
import ru.homyakin.gwent.models.InlineMenuItem;
import ru.homyakin.gwent.models.InlineResult;
import ru.homyakin.gwent.models.UserInlineQuery;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.UserNotRegistered;
import ru.homyakin.gwent.service.action.AllWinsAction;
import ru.homyakin.gwent.service.action.CurrentSeasonAction;
import ru.homyakin.gwent.service.action.GwentCardsAction;
import ru.homyakin.gwent.service.action.GwentProfileAction;

@Service
public class InlineModeService {
    private final UsersRepository usersRepository;
    private final GwentProfileAction gwentProfileAction;
    private final GwentCardsAction gwentCardsAction;
    private final AllWinsAction allWinsAction;
    private final CurrentSeasonAction currentSeasonAction;

    public InlineModeService(
        UsersRepository usersRepository,
        GwentProfileAction gwentProfileAction,
        GwentCardsAction gwentCardsAction,
        AllWinsAction allWinsAction,
        CurrentSeasonAction currentSeasonAction
    ) {
        this.usersRepository = usersRepository;
        this.gwentProfileAction = gwentProfileAction;
        this.gwentCardsAction = gwentCardsAction;
        this.allWinsAction = allWinsAction;
        this.currentSeasonAction = currentSeasonAction;
    }
    public Either<EitherError, List<InlineResult>> createInlineMenu(UserInlineQuery query) {
        var databaseResult = usersRepository.getProfileById(query.getId());
        if (databaseResult.isLeft()) {
            if (databaseResult.getLeft() instanceof UserNotRegistered) {
                return Either.right(Collections.singletonList(
                    new InlineResult(InlineMenuItem.UNKNOWN_USER, InlineMenuItem.UNKNOWN_USER.getDescription())
                ));
            }
            return Either.left(databaseResult.getLeft());
        }
        var name = databaseResult.get();
        var profile = gwentProfileAction.getProfileByName(name);
        if (profile.isLeft()) {
            return Either.left(profile.getLeft());
        }
        var cards = gwentCardsAction.getCardsByName(name);
        if (cards.isLeft()) {
            return Either.left(cards.getLeft());
        }
        var allWins = allWinsAction.getAllWinsByName(name);
        if (allWins.isLeft()) {
            return Either.left(allWins.getLeft());
        }
        var currentSeason = currentSeasonAction.getCurrentSeasonByName(name);
        if (currentSeason.isLeft()) {
            return Either.left(currentSeason.getLeft());
        }
        var results = new ArrayList<InlineResult>();
        results.add(new InlineResult(InlineMenuItem.PROFILE, profile.get().getText()));
        results.add(new InlineResult(InlineMenuItem.CARDS, cards.get().getText()));
        results.add(new InlineResult(InlineMenuItem.ALL_WINS, allWins.get().getText()));
        results.add(new InlineResult(InlineMenuItem.CURRENT_SEASON, currentSeason.get().getText()));

        return Either.right(results);
    }
}
