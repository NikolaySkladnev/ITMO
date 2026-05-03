package backend.academy.linktracker.bot.application.port.in;

import backend.academy.linktracker.bot.adapter.out.grpc.dto.TrackedLinkDto;
import java.util.List;

public interface ListTrackedLinksUseCase {
    List<TrackedLinkDto> getLinks(long chatId, String tag);
}
