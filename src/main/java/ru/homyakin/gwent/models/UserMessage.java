package ru.homyakin.gwent.models;

public class UserMessage {
    private final String text;
    private final Long id;
    private final boolean privateMessage;

    public UserMessage(String text, Long id, boolean privateMessage) {
        this.text = text;
        this.id = id;
        this.privateMessage = privateMessage;
    }

    public String getText() {
        return text;
    }

    public Long getId() {
        return id;
    }

    public boolean isPrivateMessage() {
        return privateMessage;
    }
}
