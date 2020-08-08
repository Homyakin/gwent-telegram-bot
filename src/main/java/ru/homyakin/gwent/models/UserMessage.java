package ru.homyakin.gwent.models;

public class UserMessage {
    private final String text;
    private final int id;

    public UserMessage(String text, int id) {
        this.text = text;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
