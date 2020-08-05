package ru.homyakin.gwent.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.Command;
import ru.homyakin.gwent.models.GwentProfile;
import ru.homyakin.gwent.service.actions.GwentProfileAction;

@Service
public class CommandService {
    private final static String UNKNOWN_COMMAND = "Unknown command";
    private final GwentProfileAction gwentProfileAction;

    public CommandService(GwentProfileAction gwentProfileAction) {
        this.gwentProfileAction = gwentProfileAction;
    }

    public Optional<String> executeCommand(String command) {
        if (command.toLowerCase().startsWith(Command.GET_PROFILE.getValue())) {
            var args = command.split(" ");
            if (args.length != 2) return Optional.of(UNKNOWN_COMMAND);
            var name = args[1];
            return Optional.of(gwentProfileAction.getProfile(name)
                .map(GwentProfile::toString)
                .orElse(String.format("Профиля %s не существует или он скрыт", name)));
        }
        return Optional.empty();
    }
}
