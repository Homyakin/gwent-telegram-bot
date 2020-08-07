package ru.homyakin.gwent.models.exceptions;

public class UnknownCommand extends EitherError {

    public UnknownCommand() {
        super("Unknown command");
    }
}
