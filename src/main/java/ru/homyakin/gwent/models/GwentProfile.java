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
        this.matches = "0";
        this.wins = "0";
        this.loses = "0";
        this.draws = "0";
        this.rank = rank;
    }

    @Override
    public String toString() {
        var winrate = !matches.equals("0") ? Double.parseDouble(wins.replace(",", "")) / Double.parseDouble(matches.replace(",", "")) * 100 : 0d;
        return String.format(
            ":bust_in_silhouette: %s - %s MMR\n" +
                "Престиж: %s\n" +
                "Уровень: %s\n" +
                "Ранг: %s\n" +
                "Позиция: %s\n" +
                "Матчей: %s; %.2f%% винрейт\n" +
                "Побед: %s\n" +
                "Поражений: %s\n" +
                "Ничьих: %s\n",
            name,
            mmr,
            prestige,
            level,
            rank,
            position,
            matches,
            winrate,
            wins,
            loses,
            draws
        );
    }
}
