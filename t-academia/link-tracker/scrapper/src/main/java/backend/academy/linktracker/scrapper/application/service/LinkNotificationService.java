package backend.academy.linktracker.scrapper.application.service;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.dto.UpdateNotification;
import backend.academy.linktracker.scrapper.application.port.out.BotClient;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkNotificationService {

    private static final int PREVIEW_LIMIT = 200;
    private static final String EMPTY_VALUE = "-";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'").withZone(ZoneOffset.UTC);

    private final BotClient botClient;

    public void send(String url, List<Long> chatIds, List<LinkUpdateEvent> events) {
        for (LinkUpdateEvent event : events) {
            botClient.sendUpdate(
                    new UpdateNotification(notificationId(url, event), url, formatMessage(event), chatIds));
        }
    }

    private String formatMessage(LinkUpdateEvent event) {
        return ("%s%n" + "%s: %s%n" + "Автор: %s%n" + "Создано: %s%n" + "%s: %s")
                .formatted(
                        header(event.type()),
                        titleLabel(event.type()),
                        safe(event.title()),
                        safe(event.username()),
                        formatInstant(event.createdAt()),
                        previewLabel(event.type()),
                        preview(event.content()));
    }

    private String header(LinkUpdateType type) {
        return switch (type) {
            case GITHUB_ISSUE -> "GitHub: новый Issue";
            case GITHUB_PULL_REQUEST -> "GitHub: новый PR";
            case STACKOVERFLOW_ANSWER -> "StackOverflow: новый ответ";
            case STACKOVERFLOW_COMMENT -> "StackOverflow: новый комментарий";
        };
    }

    private String titleLabel(LinkUpdateType type) {
        return switch (type) {
            case GITHUB_ISSUE, GITHUB_PULL_REQUEST -> "Название";
            case STACKOVERFLOW_ANSWER, STACKOVERFLOW_COMMENT -> "Тема";
        };
    }

    private String previewLabel(LinkUpdateType type) {
        return switch (type) {
            case GITHUB_ISSUE, GITHUB_PULL_REQUEST -> "Превью описания";
            case STACKOVERFLOW_ANSWER, STACKOVERFLOW_COMMENT -> "Превью";
        };
    }

    private String preview(String rawContent) {
        String normalized = normalizeText(rawContent);

        if (normalized.isBlank()) {
            return EMPTY_VALUE;
        }

        if (normalized.length() <= PREVIEW_LIMIT) {
            return normalized;
        }

        return normalized.substring(0, PREVIEW_LIMIT);
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String withoutHtml = value.replaceAll("<[^>]*>", " ");
        String unescaped = StringEscapeUtils.unescapeHtml4(withoutHtml);

        return unescaped.replaceAll("\\s+", " ").trim();
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return EMPTY_VALUE;
        }

        return value.trim();
    }

    private String formatInstant(Instant instant) {
        return DATE_TIME_FORMATTER.format(instant);
    }

    private long notificationId(String url, LinkUpdateEvent event) {
        String rawId = url + ":" + event.type().name() + ":" + event.externalId();
        return Integer.toUnsignedLong(rawId.hashCode());
    }
}
