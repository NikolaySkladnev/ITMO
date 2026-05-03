package backend.academy.linktracker.scrapper.adapter.out.kafka.dto;

import java.util.List;

public record LinkUpdateMessage(long id, String url, String description, List<Long> tgChatIds) {}
