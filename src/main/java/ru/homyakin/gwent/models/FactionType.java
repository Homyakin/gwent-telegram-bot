package ru.homyakin.gwent.models;

public enum FactionType {
    GENERAL("gwent", "Всего"),
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

    public static FactionType fromSiteName(String siteName) {
        return switch (siteName) {
            case "monsters" -> MONSTERS;
            case "nilfgaard" -> NILFGAARD;
            case "scoiatael" -> SCOIATAEL;
            case "skellige" -> SKELLIGE;
            case "northernrealms" -> NORTHERN_REALMS;
            case "syndicate" -> SYNDICATE;
            default -> null;
        };
    }

    public String getRusName() {
        return rusName;
    }

    public String getSiteName() {
        return siteName;
    }
}
