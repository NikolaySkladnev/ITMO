package backend.academy.linktracker.scrapper.adapter.out.http.stack.overflow;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.exception.ExternalServiceException;
import backend.academy.linktracker.scrapper.application.port.out.StackOverFlowClient;
import backend.academy.linktracker.scrapper.application.port.out.StackOverflowEventsClient;
import backend.academy.linktracker.scrapper.infrastructure.properties.StackoverflowProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriBuilder;

@Component
public class StackOverFlowHttpClient implements StackOverFlowClient, StackOverflowEventsClient {

    private static final int PAGE_SIZE = 100;

    private final RestClient restClient;
    private final String key;
    private final String accessToken;

    @Autowired
    public StackOverFlowHttpClient(RestClient.Builder builder, StackoverflowProperties properties) {
        this(builder, properties.getKey(), properties.getAccessToken());
    }

    public StackOverFlowHttpClient(RestClient.Builder builder) {
        this(builder, null, null);
    }

    private StackOverFlowHttpClient(RestClient.Builder builder, String key, String accessToken) {
        this.restClient = builder.baseUrl("https://api.stackexchange.com/2.3").build();
        this.key = key;
        this.accessToken = accessToken;
    }

    @Override
    public Instant getLastUpdate(long id) {
        try {
            QuestionResponse question = fetchQuestion(id);

            if (question.lastActivityDate() == null) {
                throw new ExternalServiceException("StackOverflow API returned an invalid body");
            }

            return Instant.ofEpochSecond(question.lastActivityDate());
        } catch (ExternalServiceException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ExternalServiceException("StackOverflow API request failed", e);
        } catch (Exception e) {
            throw new ExternalServiceException("StackOverflow API response handling failed", e);
        }
    }

    @Override
    public List<LinkUpdateEvent> getUpdates(long questionId, Instant since) {
        try {
            QuestionResponse question = fetchQuestion(questionId);
            String questionTitle = question.title();

            List<LinkUpdateEvent> result = new ArrayList<>();
            result.addAll(fetchNewAnswers(questionId, questionTitle, since));
            result.addAll(fetchNewQuestionComments(questionId, questionTitle, since));
            result.addAll(fetchNewAnswerComments(questionId, questionTitle, since));

            return result.stream()
                    .sorted(Comparator.comparing(LinkUpdateEvent::createdAt))
                    .toList();
        } catch (ExternalServiceException e) {
            throw e;
        } catch (RestClientException e) {
            throw new ExternalServiceException("StackOverflow API request failed", e);
        } catch (Exception e) {
            throw new ExternalServiceException("StackOverflow API response handling failed", e);
        }
    }

    private QuestionResponse fetchQuestion(long questionId) {
        QuestionsResponse response = getBody(
                uriBuilder -> applyDefaultQueryParameters(uriBuilder.path("/questions/{id}"))
                        .build(questionId),
                QuestionsResponse.class);

        validateResponse(response);

        if (response.items().isEmpty()) {
            throw new ExternalServiceException("StackOverflow API returned an invalid body");
        }

        return response.items().get(0);
    }

    private List<LinkUpdateEvent> fetchNewAnswers(long questionId, String questionTitle, Instant since) {
        return collectPaged(
                page -> fetchAnswersPage(questionId, "creation", since, true, page),
                since,
                item -> toInstant(item.creationDate()),
                (item, createdAt) -> new LinkUpdateEvent(
                        safeId(item.answerId()),
                        LinkUpdateType.STACKOVERFLOW_ANSWER,
                        questionTitle,
                        ownerName(item.owner()),
                        createdAt,
                        item.body()),
                ArrayList::new);
    }

    private List<LinkUpdateEvent> fetchNewQuestionComments(long questionId, String questionTitle, Instant since) {
        return collectPaged(
                page -> fetchQuestionCommentsPage(questionId, since, page),
                since,
                item -> toInstant(item.creationDate()),
                (item, createdAt) -> new LinkUpdateEvent(
                        safeId(item.commentId()),
                        LinkUpdateType.STACKOVERFLOW_COMMENT,
                        questionTitle,
                        ownerName(item.owner()),
                        createdAt,
                        item.body()),
                ArrayList::new);
    }

    private List<LinkUpdateEvent> fetchNewAnswerComments(long questionId, String questionTitle, Instant since) {
        Set<Long> answerIds = fetchAnswerIdsWithRecentActivity(questionId, since);
        if (answerIds.isEmpty()) {
            return List.of();
        }

        List<LinkUpdateEvent> result = new ArrayList<>();
        List<Long> batch = new ArrayList<>(PAGE_SIZE);

        for (Long answerId : answerIds) {
            batch.add(answerId);

            if (batch.size() == PAGE_SIZE) {
                result.addAll(fetchAnswerCommentsBatch(batch, questionTitle, since));
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            result.addAll(fetchAnswerCommentsBatch(batch, questionTitle, since));
        }

        return result;
    }

    private Set<Long> fetchAnswerIdsWithRecentActivity(long questionId, Instant since) {
        return collectPaged(
                page -> fetchAnswersPage(questionId, "activity", since, false, page),
                since,
                item -> toInstant(item.lastActivityDate()),
                (item, activityAt) -> item.answerId(),
                LinkedHashSet::new);
    }

    private List<LinkUpdateEvent> fetchAnswerCommentsBatch(List<Long> answerIds, String questionTitle, Instant since) {
        String joinedIds =
                String.join(";", answerIds.stream().map(String::valueOf).toList());

        return collectPaged(
                page -> fetchAnswerCommentsPage(joinedIds, since, page),
                since,
                item -> toInstant(item.creationDate()),
                (item, createdAt) -> new LinkUpdateEvent(
                        safeId(item.commentId()),
                        LinkUpdateType.STACKOVERFLOW_COMMENT,
                        questionTitle,
                        ownerName(item.owner()),
                        createdAt,
                        item.body()),
                ArrayList::new);
    }

    private AnswersResponse fetchAnswersPage(long questionId, String sort, Instant since, boolean withBody, int page) {
        return getBody(
                uriBuilder -> {
                    UriBuilder builder = applyDefaultQueryParameters(uriBuilder.path("/questions/{id}/answers"))
                            .queryParam("sort", sort)
                            .queryParam("order", "desc")
                            .queryParam("fromdate", since.getEpochSecond())
                            .queryParam("page", page)
                            .queryParam("pagesize", PAGE_SIZE);

                    if (withBody) {
                        builder.queryParam("filter", "withbody");
                    }

                    return builder.build(questionId);
                },
                AnswersResponse.class);
    }

    private CommentsResponse fetchQuestionCommentsPage(long questionId, Instant since, int page) {
        return getBody(
                uriBuilder -> applyDefaultQueryParameters(uriBuilder.path("/questions/{id}/comments"))
                        .queryParam("sort", "creation")
                        .queryParam("order", "desc")
                        .queryParam("fromdate", since.getEpochSecond())
                        .queryParam("filter", "withbody")
                        .queryParam("page", page)
                        .queryParam("pagesize", PAGE_SIZE)
                        .build(questionId),
                CommentsResponse.class);
    }

    private CommentsResponse fetchAnswerCommentsPage(String joinedIds, Instant since, int page) {
        return getBody(
                uriBuilder -> applyDefaultQueryParameters(uriBuilder.path("/answers/{ids}/comments"))
                        .queryParam("sort", "creation")
                        .queryParam("order", "desc")
                        .queryParam("fromdate", since.getEpochSecond())
                        .queryParam("filter", "withbody")
                        .queryParam("page", page)
                        .queryParam("pagesize", PAGE_SIZE)
                        .build(joinedIds),
                CommentsResponse.class);
    }

    private <T> T getBody(Function<UriBuilder, URI> uriFunction, Class<T> responseType) {
        return restClient
                .get()
                .uri(uriFunction)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (_, httpResponse) -> {
                    throw new ExternalServiceException("StackOverflow API returned status "
                            + httpResponse.getStatusCode().value());
                })
                .body(responseType);
    }

    private void validateResponse(PagedResponse<?> response) {
        if (response == null || response.items() == null) {
            throw new ExternalServiceException("StackOverflow API returned an invalid body");
        }
    }

    private <T, R, C extends Collection<R>> C collectPaged(
            IntFunction<? extends PagedResponse<T>> pageFetcher,
            Instant since,
            Function<T, Instant> timestampExtractor,
            BiFunction<T, Instant, R> mapper,
            Supplier<C> resultFactory) {

        C result = resultFactory.get();
        int page = 1;

        while (true) {
            PagedResponse<T> response = pageFetcher.apply(page);
            validateResponse(response);

            if (response.items().isEmpty()) {
                break;
            }

            boolean reachedSavedBoundary = false;

            for (T item : response.items()) {
                Instant timestamp = timestampExtractor.apply(item);
                if (timestamp == null) {
                    continue;
                }

                if (!timestamp.isAfter(since)) {
                    reachedSavedBoundary = true;
                    continue;
                }

                R mapped = mapper.apply(item, timestamp);
                if (mapped != null) {
                    result.add(mapped);
                }
            }

            if (reachedSavedBoundary || !Boolean.TRUE.equals(response.hasMore())) {
                break;
            }

            page++;
        }

        return result;
    }

    private UriBuilder applyDefaultQueryParameters(UriBuilder uriBuilder) {
        uriBuilder.queryParam("site", "stackoverflow");

        if (key != null && !key.isBlank()) {
            uriBuilder.queryParam("key", key);
        }

        if (accessToken != null && !accessToken.isBlank()) {
            uriBuilder.queryParam("access_token", accessToken);
        }

        return uriBuilder;
    }

    private Instant toInstant(Long epochSeconds) {
        return epochSeconds == null ? null : Instant.ofEpochSecond(epochSeconds);
    }

    private long safeId(Long value) {
        return value == null ? 0L : value;
    }

    private String ownerName(OwnerResponse owner) {
        return owner == null ? null : owner.displayName();
    }

    private interface PagedResponse<T> {
        List<T> items();

        Boolean hasMore();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record QuestionsResponse(
            List<QuestionResponse> items,
            @JsonProperty("has_more") Boolean hasMore) implements PagedResponse<QuestionResponse> {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record QuestionResponse(
            @JsonProperty("question_id") Long questionId,
            String title,
            @JsonProperty("last_activity_date") Long lastActivityDate) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AnswersResponse(
            List<AnswerResponse> items,
            @JsonProperty("has_more") Boolean hasMore) implements PagedResponse<AnswerResponse> {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AnswerResponse(
            @JsonProperty("answer_id") Long answerId,
            String body,
            @JsonProperty("creation_date") Long creationDate,
            @JsonProperty("last_activity_date") Long lastActivityDate,
            OwnerResponse owner) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CommentsResponse(
            List<CommentResponse> items,
            @JsonProperty("has_more") Boolean hasMore) implements PagedResponse<CommentResponse> {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record CommentResponse(
            @JsonProperty("comment_id") Long commentId,
            String body,
            @JsonProperty("creation_date") Long creationDate,
            OwnerResponse owner) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OwnerResponse(
            @JsonProperty("display_name") String displayName) {}
}
