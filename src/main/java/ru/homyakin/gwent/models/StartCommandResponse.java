package ru.homyakin.gwent.models;

public class StartCommandResponse extends CommandResponse {
    public StartCommandResponse() {
        super("Приветствую тебя в боте для получения информации с сайта playgwent.com.\n\n" +
            "Зарегистрируйся с помощью команды /register, или просто введи /get_profile и своё имя в Гвинте через пробел.");
    }
}
