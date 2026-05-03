package backend.academy.linktracker.scrapper.domain.entities;

import java.util.List;

public record Link(String url, List<String> tags) {}
