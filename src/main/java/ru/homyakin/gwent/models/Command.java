package ru.homyakin.gwent.models;

public enum Command {
    GET_PROFILE("/get_profile"),
    GET_CARDS("/get_cards");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
