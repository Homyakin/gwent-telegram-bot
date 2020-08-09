package ru.homyakin.gwent.models;

import java.io.InputStream;
import java.util.Optional;

public class CommandResponse {
    private final String text;
    private final InputStream image;

    public CommandResponse(String text) {
        this(text, null);
    }

    public CommandResponse(String text, InputStream image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public Optional<InputStream> getImageLink() {
        return Optional.ofNullable(image);
    }
}
