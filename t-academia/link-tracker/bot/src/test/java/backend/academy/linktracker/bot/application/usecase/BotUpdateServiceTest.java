package backend.academy.linktracker.bot.application.usecase;

import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.application.port.out.NotificationSender;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BotUpdateServiceTest {

    @Mock
    private NotificationSender notificationSender;

    @InjectMocks
    private BotUpdateService service;

    @Test
    void shouldSendUpdateWithDescription() {
        service.process(1L, "https://github.com/openai/openai-java", "Repo updated", List.of(1L, 2L));

        verify(notificationSender)
                .send(
                        "Обнаружено обновление по ссылке: https://github.com/openai/openai-java\nRepo updated",
                        List.of(1L, 2L));
    }

    @Test
    void shouldSendUpdateWithoutDescriptionWhenBlank() {
        service.process(1L, "https://stackoverflow.com/questions/1/example", "   ", List.of(99L));

        verify(notificationSender)
                .send("Обнаружено обновление по ссылке: https://stackoverflow.com/questions/1/example", List.of(99L));
    }
}
