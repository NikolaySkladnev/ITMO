package backend.academy.linktracker.scrapper.application.dto;

import java.time.Instant;

public record LinkUpdateEvent(
        long externalId, LinkUpdateType type, String title, String username, Instant createdAt, String content) {}
