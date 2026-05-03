package backend.academy.linktracker.bot.application.port.in;

public interface UntrackLinkUseCase {
    void untrack(long chatId, String url);
}
