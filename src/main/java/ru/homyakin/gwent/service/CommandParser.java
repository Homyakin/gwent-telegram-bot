package ru.homyakin.gwent.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.config.BotConfiguration;
import ru.homyakin.gwent.models.Command;

@Service
public class CommandParser {
    private final String username;

    public CommandParser(BotConfiguration botConfiguration) {
        this.username = botConfiguration.getUsername();
    }

    public Command getCommandFromText(String text) {
        var unknownCommand = text.split(" ")[0];
        for (var command : Command.values()) {
            if (
                unknownCommand.equals(command.getValue()) ||
                    unknownCommand.equals(String.format("%s@%s", command.getValue(), username))
            ) {
                return command;
            }
        }
        return Command.UNKNOWN;
    }

    public Optional<String> getNameFromText(String text) {
        var args = text.split(" ");
        if (args.length != 2) {
            return Optional.empty();
        }
        return Optional.of(args[1]);
    }
}
