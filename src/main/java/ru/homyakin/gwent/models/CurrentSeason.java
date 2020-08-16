package ru.homyakin.gwent.models;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CurrentSeason {
    private final int matches;
    private final int wins;
    private final int loses;
    private final int draws;
    private final Map<FactionType, FactionMatches> factionMatches;

    public CurrentSeason(
        int matches,
        int wins,
        int loses,
        int draws,
        Map<FactionType, FactionMatches> factionMatches
    ) {
        this.matches = matches;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
        this.factionMatches = factionMatches;
    }

    public CurrentSeason() {
        this.matches = 0;
        this.wins = 0;
        this.loses = 0;
        this.draws = 0;
        this.factionMatches = null;
    }

    public Optional<Map<FactionType, FactionMatches>> getFactionMatches() {
        return Optional.ofNullable(factionMatches);
    }

    @Override
    public String toString() {
        var winrate = matches == 0 ? 0d : (double) wins / matches * 100;
        return String.format(
            "Всего матчей: %d; %.2f%% винрейт\n" +
            "Побед: %d\n" +
            "Поражений: %d\n" +
            "Ничьих: %d\n",
            matches,
            winrate,
            wins,
            loses,
            draws
        );
    }
}
