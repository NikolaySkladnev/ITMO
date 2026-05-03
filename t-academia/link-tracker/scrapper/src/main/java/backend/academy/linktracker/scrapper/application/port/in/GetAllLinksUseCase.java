package backend.academy.linktracker.scrapper.application.port.in;

import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;

public interface GetAllLinksUseCase {
    ListLinkResult getAllLinks(long chatId);
}
