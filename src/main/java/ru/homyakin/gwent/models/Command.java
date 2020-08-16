package ru.homyakin.gwent.models;

public enum Command {
    GET_PROFILE("/get_profile"),
    GET_CARDS("/get_cards"),
    REGISTER("/register"),
    GET_ALL_WINS("/get_all_wins"),
    START("/start"),
    GET_CURRENT_SEASON("/get_current_season"),
    UNKNOWN("unknown");

    private final String value;

    Command(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
