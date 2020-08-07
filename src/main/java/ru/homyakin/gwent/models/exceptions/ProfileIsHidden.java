package ru.homyakin.gwent.models.exceptions;

public class ProfileIsHidden extends EitherError {

    public ProfileIsHidden(String name) {
        super(String.format("Профиль %s скрыт", name));
    }
}
