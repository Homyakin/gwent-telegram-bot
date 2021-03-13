package ru.homyakin.gwent.models;

public class UserInlineQuery {
    private final String text;
    private final Long id;

    public UserInlineQuery(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Long getId() {
        return id;
    }
}
