package ru.homyakin.gwent.service;

import io.vavr.control.Either;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.Command;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.UserMessage;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.InvalidCommand;
import ru.homyakin.gwent.models.errors.UnknownCommand;
import ru.homyakin.gwent.service.action.GwentCardsAction;
import ru.homyakin.gwent.service.action.GwentProfileAction;
import ru.homyakin.gwent.service.action.RegisterProfileAction;

@Service
public class CommandService {
    private final GwentProfileAction gwentProfileAction;
    private final GwentCardsAction gwentCardsAction;
    private final RegisterProfileAction registerProfileAction;

    public CommandService(
        GwentProfileAction gwentProfileAction,
        GwentCardsAction gwentCardsAction,
        RegisterProfileAction registerProfileAction
    ) {
        this.gwentProfileAction = gwentProfileAction;
        this.gwentCardsAction = gwentCardsAction;
        this.registerProfileAction = registerProfileAction;
    }

    public Either<EitherError, CommandResponse> executeCommand(UserMessage message) {
        var command = message.getText();
        if (command.toLowerCase().startsWith(Command.GET_PROFILE.getValue())) {
            var name = getNameFromCommand(command);
            return gwentProfileAction.getProfile(name, message.getId());
        } else if (command.toLowerCase().startsWith(Command.GET_CARDS.getValue())) {
            var name = getNameFromCommand(command);
            return gwentCardsAction.getCards(name, message.getId());
        } else if (command.toLowerCase().startsWith(Command.REGISTER.getValue())) {
            var name = getNameFromCommand(command);
            if (name.isEmpty()) return Either.left(new InvalidCommand("Не забывай отправить имя через пробел после команды"));
            return registerProfileAction.registerProfile(name.get(), message.getId());
        }
        return Either.left(new UnknownCommand());
    }

    private Optional<String> getNameFromCommand(String command) {
        var args = command.split(" ");
        if (args.length != 2) return Optional.empty();
        return Optional.of(args[1]);
    }
}
