package backend.academy.linktracker.scrapper.application.dto;

import java.util.List;

public record UpdateNotification(long id, String url, String description, List<Long> chatIds) {}
