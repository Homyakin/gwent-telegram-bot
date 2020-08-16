package ru.homyakin.gwent.models;

public class FactionMatches {
    private final FactionType factionType;
    private final int matches;

    public FactionMatches(FactionType factionType, int matches) {
        this.factionType = factionType;
        this.matches = matches;
    }

    public FactionType getFactionType() {
        return factionType;
    }

    public int getMatches() {
        return matches;
    }

    @Override
    public String toString() {
        return String.format(
            "%s: %d",
            factionType.getRusName(),
            matches
        );
    }
}
