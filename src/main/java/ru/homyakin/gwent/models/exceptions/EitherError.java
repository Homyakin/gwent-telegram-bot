package ru.homyakin.gwent.models.exceptions;

//TODO make sealed when release
public abstract class EitherError {
    private final String message;

    public EitherError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
