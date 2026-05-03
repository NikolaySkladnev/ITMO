package backend.academy.linktracker.scrapper.application.port.in;

import backend.academy.linktracker.scrapper.application.dto.LinkResult;
import java.util.List;

public interface AddLinkUseCase {
    LinkResult addLink(long chatId, String url, List<String> tags, List<String> filters);
}
