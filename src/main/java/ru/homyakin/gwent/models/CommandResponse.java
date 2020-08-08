package ru.homyakin.gwent.models;

import java.util.Optional;

public class CommandResponse {
    private final String text;
    private final String imageLink;

    public CommandResponse(String text) {
        this(text, null);
    }

    public CommandResponse(String text, String imageLink) {
        this.text = text;
        this.imageLink = imageLink;
    }

    public String getText() {
        return text;
    }

    public Optional<String> getImageLink() {
        return Optional.ofNullable(imageLink);
    }
}
