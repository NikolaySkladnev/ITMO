package backend.academy.linktracker.scrapper.application.port.out;

import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatDataRepository {

    int DEFAULT_PAGE_SIZE = 500;

    boolean addChat(Chat chat);

    boolean removeChat(Chat chat);

    boolean hasChat(Chat chat);

    boolean addLinkToChat(Chat chat, Link link);

    Optional<Link> removeLinkFromChat(Chat chat, Link link);

    Optional<Link> findLinkByChat(Chat chat, String url);

    default List<Link> getAllLinksByChat(Chat chat) {
        List<Link> result = new ArrayList<>();
        int offset = 0;

        while (true) {
            List<Link> page = getAllLinksByChat(chat, offset, DEFAULT_PAGE_SIZE);
            if (page.isEmpty()) {
                return result;
            }

            result.addAll(page);
            if (page.size() < DEFAULT_PAGE_SIZE) {
                return result;
            }

            offset += page.size();
        }
    }

    List<Link> getAllLinksByChat(Chat chat, int offset, int limit);

    default Set<String> getAllTrackedLinks() {
        Set<String> result = new LinkedHashSet<>();
        int offset = 0;

        while (true) {
            List<String> page = getTrackedLinksPage(offset, DEFAULT_PAGE_SIZE);
            if (page.isEmpty()) {
                return result;
            }

            result.addAll(page);
            if (page.size() < DEFAULT_PAGE_SIZE) {
                return result;
            }

            offset += page.size();
        }
    }

    List<String> getTrackedLinksPage(int offset, int limit);

    default List<Chat> getAllChatByLink(Link link) {
        List<Chat> result = new ArrayList<>();
        int offset = 0;

        while (true) {
            List<Chat> page = getAllChatByLink(link, offset, DEFAULT_PAGE_SIZE);
            if (page.isEmpty()) {
                return result;
            }

            result.addAll(page);
            if (page.size() < DEFAULT_PAGE_SIZE) {
                return result;
            }

            offset += page.size();
        }
    }

    List<Chat> getAllChatByLink(Link link, int offset, int limit);

    Instant getLastUpdateTime(Link link);

    void setLastUpdateTime(Link link, Instant lastUpdateTime);
}
