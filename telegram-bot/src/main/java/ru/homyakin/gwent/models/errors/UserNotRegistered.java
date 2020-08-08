package ru.homyakin.gwent.models.errors;

public class UserNotRegistered extends EitherError {

    public UserNotRegistered(String message) {
        super(message);
    }
}
