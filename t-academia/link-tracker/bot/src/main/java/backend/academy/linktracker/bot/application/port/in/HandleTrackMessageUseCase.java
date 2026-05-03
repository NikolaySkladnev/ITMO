package backend.academy.linktracker.bot.application.port.in;

public interface HandleTrackMessageUseCase {
    void handle(long chatId, String text);
}
