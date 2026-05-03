package backend.academy.linktracker.scrapper.application.port.in;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;

public interface RemoveLinkUseCase {
    LinkResult removeLink(long chatId, String url);
}
