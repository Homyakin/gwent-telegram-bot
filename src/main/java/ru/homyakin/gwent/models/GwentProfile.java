package ru.homyakin.gwent.models;

public class GwentProfile {
    private final String name;
    private final String level;
    private final String prestige;
    private final String mmr;
    private final String position;
    private final String rank;
    private final CurrentSeason currentSeason;

    public GwentProfile(
        String name,
        String level,
        String prestige,
        String mmr,
        String position,
        String rank,
        CurrentSeason currentSeason
    ) {
        this.name = name;
        this.level = level;
        this.prestige = prestige;
        this.mmr = mmr;
        this.position = position;
        this.rank = rank;
        this.currentSeason = currentSeason;
    }

    public GwentProfile(
        String name,
        String level,
        String prestige,
        String mmr,
        String position,
        String rank
    ) {
        this.name = name;
        this.level = level;
        this.prestige = prestige;
        this.mmr = mmr;
        this.position = position;
        this.rank = rank;
        this.currentSeason = new CurrentSeason();
    }

    @Override
    public String toString() {
        return String.format(
            ":bust_in_silhouette: %s - %s MMR\n" +
                "Престиж: %s\n" +
                "Уровень: %s\n" +
                "Ранг: %s\n" +
                "Позиция: %s\n" +
                "Текущий сезон:\n" +
                "%s",
            name,
            mmr,
            prestige,
            level,
            rank,
            position,
            currentSeason.toString()
        );
    }
}
