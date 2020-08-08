package ru.homyakin.gwent.models.errors;

public class InternalAppError extends EitherError {

    public InternalAppError() {
        super("Неизвестная ошибка");
    }
}
