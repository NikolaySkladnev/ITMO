package backend.academy.linktracker.scrapper.application.dto;

import java.util.List;

public record LinkProcessingReport(int checkedLinks, int foundUpdates, List<String> failedLinks) {

    public LinkProcessingReport {
        failedLinks = failedLinks == null ? List.of() : List.copyOf(failedLinks);
    }
}
