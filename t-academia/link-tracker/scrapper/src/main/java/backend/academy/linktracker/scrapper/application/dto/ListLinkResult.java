package backend.academy.linktracker.scrapper.application.dto;

import java.util.List;

public record ListLinkResult(List<LinkResult> links, int size) {}
