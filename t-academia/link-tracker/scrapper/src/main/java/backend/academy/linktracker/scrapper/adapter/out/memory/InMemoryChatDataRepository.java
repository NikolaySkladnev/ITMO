package backend.academy.linktracker.scrapper.adapter.out.memory;

import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.database", name = "access-type", havingValue = "in-memory")
public class InMemoryChatDataRepository implements ChatDataRepository {

    private final Map<Chat, Map<String, Link>> storageChatLink = new ConcurrentHashMap<>();
    private final Map<String, Set<Chat>> storageLinkChat = new ConcurrentHashMap<>();
    private final Map<String, Instant> storageLinkUpdateTime = new ConcurrentHashMap<>();

    @Override
    public boolean addChat(Chat chat) {
        return storageChatLink.putIfAbsent(chat, new ConcurrentHashMap<>()) == null;
    }

    @Override
    public boolean removeChat(Chat chat) {
        Map<String, Link> links = storageChatLink.remove(chat);
        if (links == null) {
            return false;
        }

        for (String url : links.keySet()) {
            Set<Chat> chats = storageLinkChat.get(url);
            if (chats != null) {
                chats.remove(chat);
                if (chats.isEmpty()) {
                    storageLinkChat.remove(url);
                    storageLinkUpdateTime.remove(url);
                }
            }
        }

        return true;
    }

    @Override
    public boolean hasChat(Chat chat) {
        return storageChatLink.containsKey(chat);
    }

    @Override
    public boolean addLinkToChat(Chat chat, Link link) {
        Map<String, Link> links = storageChatLink.get(chat);
        if (links == null) {
            return false;
        }

        Link previous = links.putIfAbsent(link.url(), link);
        if (previous != null) {
            return false;
        }

        storageLinkChat
                .computeIfAbsent(link.url(), ignored -> ConcurrentHashMap.newKeySet())
                .add(chat);

        return true;
    }

    @Override
    public Optional<Link> removeLinkFromChat(Chat chat, Link link) {
        Map<String, Link> links = storageChatLink.get(chat);
        if (links == null) {
            return Optional.empty();
        }

        Link removed = links.remove(link.url());
        if (removed == null) {
            return Optional.empty();
        }

        Set<Chat> chats = storageLinkChat.get(link.url());
        if (chats != null) {
            chats.remove(chat);
            if (chats.isEmpty()) {
                storageLinkChat.remove(link.url());
                storageLinkUpdateTime.remove(link.url());
            }
        }

        return Optional.of(removed);
    }

    @Override
    public Optional<Link> findLinkByChat(Chat chat, String url) {
        Map<String, Link> links = storageChatLink.get(chat);
        if (links == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(links.get(url));
    }

    @Override
    public List<Link> getAllLinksByChat(Chat chat, int offset, int limit) {
        Map<String, Link> links = storageChatLink.get(chat);
        if (links == null) {
            return List.of();
        }

        return links.values().stream()
                .sorted(Comparator.comparing(Link::url))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public List<String> getTrackedLinksPage(int offset, int limit) {
        return storageLinkChat.keySet().stream()
                .sorted()
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public List<Chat> getAllChatByLink(Link link, int offset, int limit) {
        Set<Chat> chats = storageLinkChat.get(link.url());
        if (chats == null) {
            return List.of();
        }

        return chats.stream()
                .sorted(Comparator.comparingLong(Chat::chatId))
                .skip(offset)
                .limit(limit)
                .toList();
    }

    @Override
    public Instant getLastUpdateTime(Link link) {
        return storageLinkUpdateTime.get(link.url());
    }

    @Override
    public void setLastUpdateTime(Link link, Instant lastUpdateTime) {
        storageLinkUpdateTime.put(link.url(), lastUpdateTime);
    }
}
