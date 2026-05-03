package backend.academy.linktracker.scrapper.application.service;

import backend.academy.linktracker.scrapper.application.dto.LinkProcessingReport;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.exception.ExternalServiceException;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import backend.academy.linktracker.scrapper.infrastructure.properties.SchedulerProperties;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackedLinkBatchProcessor {

    private final ChatDataRepository chatDataRepository;
    private final LinkUpdateResolver linkUpdateResolver;
    private final LinkUpdateQueryService linkUpdateQueryService;
    private final LinkNotificationService linkNotificationService;
    private final SchedulerProperties schedulerProperties;

    @Qualifier("linkUpdatesExecutor")
    private final ExecutorService linkUpdatesExecutor;

    public LinkProcessingReport processAll() {
        int offset = 0;
        int checkedLinks = 0;
        int foundUpdates = 0;
        List<String> failedLinks = new ArrayList<>();

        while (true) {
            List<String> batch = chatDataRepository.getTrackedLinksPage(offset, schedulerProperties.getBatchSize());
            if (batch.isEmpty()) {
                break;
            }

            LinkProcessingReport batchReport = processBatch(batch);
            checkedLinks += batchReport.checkedLinks();
            foundUpdates += batchReport.foundUpdates();
            failedLinks.addAll(batchReport.failedLinks());

            if (batch.size() < schedulerProperties.getBatchSize()) {
                break;
            }

            offset += batch.size();
        }

        return new LinkProcessingReport(checkedLinks, foundUpdates, failedLinks);
    }

    private LinkProcessingReport processBatch(List<String> batch) {
        if (schedulerProperties.getParallelism() <= 1 || batch.size() <= 1) {
            return processChunk(batch);
        }

        List<List<String>> partitions = partition(batch, schedulerProperties.getParallelism());
        List<Future<LinkProcessingReport>> futures = new ArrayList<>();

        for (List<String> partition : partitions) {
            futures.add(linkUpdatesExecutor.submit(() -> processChunk(partition)));
        }

        int foundUpdates = 0;
        List<String> failedLinks = new ArrayList<>();

        for (int i = 0; i < futures.size(); i++) {
            try {
                LinkProcessingReport report = futures.get(i).get();
                foundUpdates += report.foundUpdates();
                failedLinks.addAll(report.failedLinks());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                failedLinks.addAll(partitions.get(i));
                log.error("Link updates batch processing was interrupted", e);
            } catch (ExecutionException e) {
                failedLinks.addAll(partitions.get(i));
                log.error("Link updates batch processing failed", e.getCause());
            }
        }

        return new LinkProcessingReport(batch.size(), foundUpdates, failedLinks);
    }

    private LinkProcessingReport processChunk(List<String> chunk) {
        int foundUpdates = 0;
        List<String> failedLinks = new ArrayList<>();

        for (String url : chunk) {
            foundUpdates += processSingleLink(url, failedLinks);
        }

        return new LinkProcessingReport(chunk.size(), foundUpdates, failedLinks);
    }

    private int processSingleLink(String url, List<String> failedLinks) {
        try {
            Link link = new Link(url, List.of());
            Instant savedLastUpdated = chatDataRepository.getLastUpdateTime(link);

            if (savedLastUpdated == null) {
                Instant initialTimestamp = linkUpdateResolver.resolveLastUpdate(url);
                chatDataRepository.setLastUpdateTime(link, initialTimestamp);
                return 0;
            }

            List<LinkUpdateEvent> updates = linkUpdateQueryService.findUpdates(url, savedLastUpdated);

            if (updates.isEmpty()) {
                return 0;
            }

            List<Long> chatIds = chatDataRepository.getAllChatByLink(link).stream()
                    .map(Chat::chatId)
                    .toList();

            if (!chatIds.isEmpty()) {
                linkNotificationService.send(url, chatIds, updates);
            }

            Instant newestUpdateTime = updates.stream()
                    .map(LinkUpdateEvent::createdAt)
                    .max(Comparator.naturalOrder())
                    .orElse(savedLastUpdated);

            chatDataRepository.setLastUpdateTime(link, newestUpdateTime);

            return updates.size();
        } catch (ExternalServiceException | IllegalArgumentException e) {
            failedLinks.add(url);
            log.warn("Skipping url={} because of known error: {}", url, e.getMessage());
            return 0;
        } catch (Exception e) {
            failedLinks.add(url);
            log.error("Unexpected error while processing url={}", url, e);
            return 0;
        }
    }

    private List<List<String>> partition(List<String> batch, int parallelism) {
        int parts = Math.min(parallelism, batch.size());
        int chunkSize = (batch.size() + parts - 1) / parts;

        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < batch.size(); i += chunkSize) {
            int toIndex = Math.min(batch.size(), i + chunkSize);
            result.add(new ArrayList<>(batch.subList(i, toIndex)));
        }

        return result;
    }
}
