package ru.homyakin.gwent.models;

public class UserInlineQuery {
    private final String text;
    private final int id;

    public UserInlineQuery(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
