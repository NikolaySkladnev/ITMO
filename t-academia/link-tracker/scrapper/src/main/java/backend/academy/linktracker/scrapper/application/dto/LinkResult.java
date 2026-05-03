package backend.academy.linktracker.scrapper.application.dto;

import java.util.List;

public record LinkResult(long id, String url, List<String> tags, List<String> filters) {}
