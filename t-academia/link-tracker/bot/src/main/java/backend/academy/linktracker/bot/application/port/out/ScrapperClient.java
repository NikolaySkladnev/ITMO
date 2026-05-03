package backend.academy.linktracker.bot.application.port.out;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import java.util.List;

public interface ScrapperClient {
    void registerChat(long chatId);

    void deleteChat(long chatId);

    void addLink(long chatId, String url, List<String> tags);

    void removeLink(long chatId, String url);

    List<TrackedLinkDto> getLinks(long chatId);
}
