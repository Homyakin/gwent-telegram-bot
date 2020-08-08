package ru.homyakin.gwent.models.errors;

public class ProfileIsHidden extends EitherError {

    public ProfileIsHidden(String name) {
        super(String.format("Профиль %s скрыт", name));
    }
}
