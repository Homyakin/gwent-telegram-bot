package ru.homyakin.gwent.models.exceptions;

public class ProfileNotFound extends EitherError {

    public ProfileNotFound(String name) {
        super(String.format("Профиль %s не существует", name));
    }
}
