package ru.homyakin.gwent.service;

import io.vavr.control.Either;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.StartCommandResponse;
import ru.homyakin.gwent.models.UnknownCommandResponse;
import ru.homyakin.gwent.models.UserMessage;
import ru.homyakin.gwent.models.errors.EitherError;
import ru.homyakin.gwent.models.errors.InvalidCommand;
import ru.homyakin.gwent.models.errors.UnknownCommand;
import ru.homyakin.gwent.service.action.AllWinsAction;
import ru.homyakin.gwent.service.action.GwentCardsAction;
import ru.homyakin.gwent.service.action.GwentProfileAction;
import ru.homyakin.gwent.service.action.RegisterProfileAction;

@Service
public class CommandService {
    private final GwentProfileAction gwentProfileAction;
    private final GwentCardsAction gwentCardsAction;
    private final RegisterProfileAction registerProfileAction;
    private final AllWinsAction allWinsAction;
    private final CommandParser commandParser;

    public CommandService(
        GwentProfileAction gwentProfileAction,
        GwentCardsAction gwentCardsAction,
        RegisterProfileAction registerProfileAction,
        AllWinsAction allWinsAction,
        CommandParser commandParser
    ) {
        this.gwentProfileAction = gwentProfileAction;
        this.gwentCardsAction = gwentCardsAction;
        this.registerProfileAction = registerProfileAction;
        this.allWinsAction = allWinsAction;
        this.commandParser = commandParser;
    }

    public Either<EitherError, CommandResponse> executeCommand(UserMessage message) {
        var command = commandParser.getCommandFromText(message.getText());
        return switch (command) {
            case GET_PROFILE -> {
                var name = commandParser.getNameFromText(message.getText());
                yield gwentProfileAction.getProfile(name, message.getId());
            }
            case GET_CARDS -> {
                var name = commandParser.getNameFromText(message.getText());
                yield gwentCardsAction.getCards(name, message.getId());
            }
            case GET_ALL_WINS -> {
                var name = commandParser.getNameFromText(message.getText());
                yield allWinsAction.getAllWins(name, message.getId());
            }
            case REGISTER -> {
                var name = commandParser.getNameFromText(message.getText());
                if (name.isEmpty()) {
                    yield Either.left(new InvalidCommand("Не забывай отправить имя через пробел после команды"));
                } else {
                    yield registerProfileAction.registerProfile(name.get(), message.getId());
                }
            }
            case START -> {
                if (message.isPrivateMessage()) {
                    yield Either.right(new StartCommandResponse());
                } else {
                    yield Either.left(new UnknownCommand());
                }
            }
            case UNKNOWN -> {
                if (message.isPrivateMessage()) {
                    yield Either.right(new UnknownCommandResponse());
                } else {
                    yield Either.left(new UnknownCommand());
                }
            }
        };
    }
}
