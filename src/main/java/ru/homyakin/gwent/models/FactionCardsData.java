package ru.homyakin.gwent.models;

public class FactionCardsData {
    private final FactionType factionType;
    private final int cards;
    private final int totalCards;

    public FactionCardsData(FactionType factionType, int cards, int totalCards) {
        this.factionType = factionType;
        this.cards = cards;
        this.totalCards = totalCards;
    }

    @Override
    public String toString() {
        return String.format(
          "%s: %d / %d",
            factionType.getRusName(),
            cards,
            totalCards
        );
    }
}
