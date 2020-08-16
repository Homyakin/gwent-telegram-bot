package ru.homyakin.gwent.models;

public class FactionCards {
    private final FactionType factionType;
    private final int cards;
    private final int totalCards;

    public FactionCards(FactionType factionType, int cards, int totalCards) {
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
