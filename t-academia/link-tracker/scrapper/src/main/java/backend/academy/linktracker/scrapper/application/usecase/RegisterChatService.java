package backend.academy.linktracker.scrapper.application.usecase;

import backend.academy.linktracker.scrapper.application.port.in.RegisterChatUseCase;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegisterChatService implements RegisterChatUseCase {

    private final ChatDataRepository chatDataRepository;

    @Override
    public void registerChat(long chatId) {
        Chat chat = new Chat(chatId);

        if (chatDataRepository.hasChat(chat)) {
            throw new IllegalStateException("Chat already exists: " + chatId);
        }

        boolean added = chatDataRepository.addChat(chat);

        if (!added) {
            throw new IllegalStateException("Chat already exists: " + chatId);
        }
    }
}
