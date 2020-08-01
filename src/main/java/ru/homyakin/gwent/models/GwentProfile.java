package ru.homyakin.gwent.models;

public class GwentProfile {
    private final String name;
    private final String level;
    private final String prestige;
    private final String mmr;
    private final String position;
    private final String matches;
    private final String wins;
    private final String loses;
    private final String draws;
    private final String rank;

    public GwentProfile(
        String name,
        String level,
        String prestige,
        String mmr,
        String position,
        String matches,
        String wins,
        String loses,
        String draws,
        String rank
    ) {
        this.name = name;
        this.level = level;
        this.prestige = prestige;
        this.mmr = mmr;
        this.position = position;
        this.matches = matches;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
        this.rank = rank;
    }

    @Override
    public String toString() {
        var winrate = Double.parseDouble(wins) / Double.parseDouble(matches) * 100;
        return String.format(
            "%s\n" +
                "Уровень: %s.%s\n" +
                "Ранг: %s; %s MMR; позиция: %s\n" +
                "Матчей: %s; %.2f%% винрейт\n" +
                "Побед: %s\n" +
                "Поражений: %s\n" +
                "Ничьих: %s\n",
            name,
            prestige,
            level,
            rank,
            mmr,
            position,
            matches,
            winrate,
            wins,
            loses,
            draws
        );
    }
}
