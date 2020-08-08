package ru.homyakin.gwent.service.action;

import io.vavr.control.Either;
import org.springframework.stereotype.Service;
import ru.homyakin.gwent.database.UsersRepository;
import ru.homyakin.gwent.models.CommandResponse;
import ru.homyakin.gwent.models.GwentProfile;
import ru.homyakin.gwent.models.errors.EitherError;

@Service
public class RegisterProfileAction {
    private final UsersRepository usersRepository;
    private final GwentProfileAction gwentProfileAction;

    public RegisterProfileAction(
        UsersRepository usersRepository,
        GwentProfileAction gwentProfileAction
    ) {
        this.usersRepository = usersRepository;
        this.gwentProfileAction = gwentProfileAction;
    }

    public Either<EitherError, CommandResponse> registerProfile(String gwentName, Integer userId) {
        var profileResult = gwentProfileAction.getProfileByName(gwentName);
        if (profileResult.isLeft()) {
            return Either.left(profileResult.getLeft());
        }
        var result = usersRepository.registerProfile(gwentName, userId);
        return result.map(CommandResponse::new);
    }
}
