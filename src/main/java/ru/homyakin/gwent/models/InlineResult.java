package ru.homyakin.gwent.models;

public class InlineResult {
    private final InlineMenuItem inlineMenuItem;
    private final String text;

    public InlineResult(InlineMenuItem inlineMenuItem, String text) {
        this.inlineMenuItem = inlineMenuItem;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public InlineMenuItem getInlineMenuItem() {
        return inlineMenuItem;
    }
}
