package backend.academy.linktracker.scrapper.application.usecase;

import backend.academy.linktracker.scrapper.application.port.in.DeleteChatUseCase;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteChatService implements DeleteChatUseCase {

    private final ChatDataRepository chatDataRepository;

    @Override
    public void deleteChat(long chatId) {
        boolean removed = chatDataRepository.removeChat(new Chat(chatId));
        if (!removed) {
            throw new IllegalArgumentException("Chat not found: " + chatId);
        }
    }
}
