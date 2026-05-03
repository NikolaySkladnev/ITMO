package backend.academy.linktracker.bot.adapter.in.telegram.commands;

import com.pengrad.telegrambot.model.Update;

public interface Command {
    String name();

    String description();

    void handle(Update update);
}
