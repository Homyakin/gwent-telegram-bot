package ru.homyakin.gwent.models.exceptions;

public class InvalidCommand extends EitherError {

    public InvalidCommand(String message) {
        super(message);
    }
}
