package ru.homyakin.gwent.models.errors;

public class InvalidCommand extends EitherError {

    public InvalidCommand(String message) {
        super(message);
    }
}
