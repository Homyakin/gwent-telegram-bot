package ru.homyakin.gwent.models.errors;

public class CurrentSeasonNotPresent extends EitherError {
    public CurrentSeasonNotPresent(String name) {
        super(String.format("В профиле %s не отображена информация о текущем сезоне", name));
    }
}
