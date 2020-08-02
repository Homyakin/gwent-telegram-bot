package ru.homyakin.gwent.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.models.Command;
import ru.homyakin.gwent.models.GwentProfile;

@Service
public class CommandService {
    private final static String UNKNOWN_COMMAND = "Unknown command";
    private final GwentProfileService gwentProfileService;

    public CommandService(GwentProfileService gwentProfileService) {
        this.gwentProfileService = gwentProfileService;
    }

    public Optional<String> executeCommand(String command) {
        if (command.toLowerCase().startsWith(Command.GET_PROFILE.getValue())) {
            var args = command.split(" ");
            if (args.length != 2) return Optional.of(UNKNOWN_COMMAND);
            var name = args[1];
            return Optional.of(gwentProfileService.getProfile(name)
                .map(GwentProfile::toString)
                .orElse(String.format("Профиля %s не существует или он скрыт", name)));
        }
        return Optional.empty();
    }
}
