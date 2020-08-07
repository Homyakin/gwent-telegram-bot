package ru.homyakin.gwent.models.exceptions;

public class InternalAppError extends EitherError {

    public InternalAppError() {
        super("Неизвестная ошибка");
    }
}
