package backend.academy.linktracker.bot.adapter.in.telegram;

import backend.academy.linktracker.bot.adapter.in.telegram.commands.Command;
import backend.academy.linktracker.bot.application.port.out.PendingLinkRepository;
import backend.academy.linktracker.bot.application.port.out.UserStateRepository;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkTrackerBot {

    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Неизвестная команда. Воспользуйтесь /help, чтобы посмотреть список доступных команд.";

    private final TelegramBot bot;
    private final UserStateRepository userStateRepository;
    private final PendingLinkRepository pendingLinkRepository;
    private final TrackHandler trackHandler;
    private final List<Command> commands;

    private Map<String, Command> commandsByName;

    @PostConstruct
    private void init() {
        commandsByName = commands.stream().collect(Collectors.toUnmodifiableMap(Command::name, Function.identity()));

        BotCommand[] menu = commands.stream()
                .map(c -> new BotCommand(c.name(), c.description()))
                .toArray(BotCommand[]::new);

        bot.execute(new SetMyCommands(menu));
        bot.setUpdatesListener(this::handler);
    }

    private int handler(List<Update> updates) {
        for (Update update : updates) {
            Message message = update.message();
            if (message == null) {
                continue;
            }

            String text = message.text();
            if (text == null || text.isBlank()) {
                continue;
            }

            long chatId = message.chat().id();

            if (text.startsWith("/")) {
                String commandName = text.trim().split("\\s+")[0];

                if (!"/cancel".equals(commandName) && userStateRepository.getState(chatId) != null) {
                    userStateRepository.clearState(chatId);
                    pendingLinkRepository.clear(chatId);
                }

                Command command = commandsByName.get(commandName);
                if (command != null) {
                    command.handle(update);
                } else {
                    defaultHandler(message);
                }
            } else {
                trackHandler.handle(update);
            }
        }

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void defaultHandler(Message message) {
        bot.execute(new SendMessage(message.chat().id().longValue(), UNKNOWN_COMMAND_MESSAGE));
    }
}
