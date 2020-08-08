package ru.homyakin.gwent.models.errors;

public class ProfileNotFound extends EitherError {

    public ProfileNotFound(String name) {
        super(String.format("Профиль %s не существует", name));
    }
}
