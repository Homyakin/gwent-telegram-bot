package ru.homyakin.gwent.service;

import io.vavr.control.Either;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.Command;
import ru.homyakin.gwent.models.exceptions.EitherError;
import ru.homyakin.gwent.models.exceptions.InvalidCommand;
import ru.homyakin.gwent.models.exceptions.UnknownCommand;
import ru.homyakin.gwent.service.action.GwentCardsAction;
import ru.homyakin.gwent.service.action.GwentProfileAction;

@Service
public class CommandService {
    private final static String UNKNOWN_COMMAND = "Unknown command";
    private final GwentProfileAction gwentProfileAction;
    private final GwentCardsAction gwentCardsAction;

    public CommandService(
        GwentProfileAction gwentProfileAction,
        GwentCardsAction gwentCardsAction
    ) {
        this.gwentProfileAction = gwentProfileAction;
        this.gwentCardsAction = gwentCardsAction;
    }

    public Either<EitherError, String> executeCommand(String command) {
        if (command.toLowerCase().startsWith(Command.GET_PROFILE.getValue())) {
            var args = command.split(" ");
            if (args.length != 2) return Either.left(new InvalidCommand("Не забывай отправить имя"));
            var name = args[1];
            return gwentProfileAction.getProfile(name);
        } else if (command.toLowerCase().startsWith(Command.GET_CARDS.getValue())) {
            var args = command.split(" ");
            if (args.length != 2) return Either.left(new InvalidCommand("Не забывай отправить имя"));
            var name = args[1];
            return gwentCardsAction.getCards(name);
        }
        return Either.left(new UnknownCommand());
    }
}
