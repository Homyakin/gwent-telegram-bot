package ru.homyakin.gwent;

import io.micronaut.configuration.picocli.PicocliRunner;
import picocli.CommandLine;

@CommandLine.Command(name = "gwent-telegram-bot")
public class Application implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello, world");
    }

    public static void main(String... args) {
        PicocliRunner.run(Application.class, args);
    }
}
