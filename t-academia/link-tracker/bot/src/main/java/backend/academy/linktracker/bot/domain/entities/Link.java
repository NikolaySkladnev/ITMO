package backend.academy.linktracker.bot.domain.entities;

import java.util.List;

public record Link(String url, List<String> tags) {
    public Link withTags(List<String> newTags) {
        return new Link(url, List.copyOf(newTags));
    }

    public static Link pending(String url) {
        return new Link(url, List.of());
    }
}
