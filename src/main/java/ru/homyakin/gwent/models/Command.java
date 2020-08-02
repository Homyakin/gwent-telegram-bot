package ru.homyakin.gwent.models;

public enum Command {
    GET_PROFILE("/get_profile");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
