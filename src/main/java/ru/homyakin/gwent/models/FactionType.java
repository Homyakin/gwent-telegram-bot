package ru.homyakin.gwent.models;

public enum  FactionType {
    GENERAL("gwent", "Всего карт"),
    MONSTERS("monsters", "Монстры"),
    NILFGAARD("nilfgaard", "Нильфгаард"),
    SCOIATAEL("scoiatael", "Скоя’таэли"),
    SKELLIGE("skellige", "Скеллиге"),
    NORTHERN_REALMS("northernrealms", "Королевства Севера"),
    SYNDICATE("syndicate", "Синдикат"),
    NEUTRAL("neutral", "Нейтральные");

    private final String siteName;
    private final String rusName;

    FactionType(String siteName, String rusName) {
        this.siteName = siteName;
        this.rusName = rusName;
    }

    public String getRusName() {
        return rusName;
    }

    public String getSiteName() {
        return siteName;
    }
}
