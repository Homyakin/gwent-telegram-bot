package ru.homyakin.gwent.models.errors;

public class UnknownCommand extends EitherError {

    public UnknownCommand() {
        super("Unknown command");
    }
}
