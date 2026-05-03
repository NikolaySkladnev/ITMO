package backend.academy.linktracker.bot.support;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public final class TelegramTestSupport {

    private TelegramTestSupport() {}

    public static Update update(long chatId, String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Chat chat = mock(Chat.class);

        lenient().when(update.message()).thenReturn(message);
        lenient().when(message.chat()).thenReturn(chat);
        lenient().when(chat.id()).thenReturn(chatId);
        lenient().when(message.text()).thenReturn(text);

        return update;
    }

    public static String textOf(SendMessage sendMessage) {
        return sendMessage.getParameters().get("text").toString();
    }
}
