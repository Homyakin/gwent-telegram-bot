package ru.homyakin.gwent.models;

public enum InlineMenuItem {
    PROFILE("profile", "Профиль", "Получить профиль"),
    CURRENT_SEASON("current_season", "Сезон", "Получить информацию о текущем сезоне"),
    CARDS("cards", "Карты", "Получить информацию о коллекции"),
    ALL_WINS("all_wins", "Все победы", "Получить победы за всё время"),
    UNKNOWN_USER("unknown", "Unknown", "Зарегистрируйся в боте, чтобы использовать инлайн");

    private final String id;
    private final String title;
    private final String description;

    InlineMenuItem(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }
}
