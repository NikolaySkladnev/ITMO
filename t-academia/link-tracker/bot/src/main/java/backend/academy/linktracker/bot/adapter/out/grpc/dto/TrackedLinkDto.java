package backend.academy.linktracker.bot.adapter.out.grpc.dto;

import java.util.List;

public record TrackedLinkDto(long id, String url, List<String> tags) {}
