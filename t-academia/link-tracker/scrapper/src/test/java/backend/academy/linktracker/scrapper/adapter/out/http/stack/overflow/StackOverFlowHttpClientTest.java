package backend.academy.linktracker.scrapper.adapter.out.http.stack.overflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import backend.academy.linktracker.scrapper.application.dto.LinkUpdateEvent;
import backend.academy.linktracker.scrapper.application.dto.LinkUpdateType;
import backend.academy.linktracker.scrapper.application.exception.ExternalServiceException;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class StackOverFlowHttpClientTest {

    private RestClient.Builder builder;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
    }

    @Test
    void shouldReturnLastActivityDate() {
        StackOverFlowHttpClient client = new StackOverFlowHttpClient(builder);

        server.expect(requestTo("https://api.stackexchange.com/2.3/questions/123?site=stackoverflow"))
                .andRespond(withSuccess("""
                        {
                          "items": [
                            {
                              "last_activity_date": 1772360130
                            }
                          ]
                        }
                        """, APPLICATION_JSON));

        Instant result = client.getLastUpdate(123L);

        assertThat(result).isEqualTo(Instant.ofEpochSecond(1772360130L));
    }

    @Test
    void shouldReturnAnswersAndCommentsCreatedAfterTimestamp() {
        StackOverFlowHttpClient client = new StackOverFlowHttpClient(builder);
        Instant since = Instant.ofEpochSecond(1772360000L);

        server.expect(once(), requestTo("https://api.stackexchange.com/2.3/questions/123?site=stackoverflow"))
                .andRespond(withSuccess("""
                        {
                          "items": [
                            {
                              "question_id": 123,
                              "title": "Question title",
                              "last_activity_date": 1772361300
                            }
                          ]
                        }
                        """, APPLICATION_JSON));

        server.expect(once(), requestTo(startsWith("https://api.stackexchange.com/2.3/questions/123/answers?")))
                .andRespond(withSuccess("""
                        {
                          "items": [
                            {
                              "answer_id": 10,
                              "body": "<p>Answer body</p>",
                              "creation_date": 1772361000,
                              "last_activity_date": 1772361200,
                              "owner": {
                                "display_name": "Alice"
                              }
                            }
                          ],
                          "has_more": false
                        }
                        """, APPLICATION_JSON));

        server.expect(once(), requestTo(startsWith("https://api.stackexchange.com/2.3/questions/123/comments?")))
                .andRespond(withSuccess("""
                        {
                          "items": [
                            {
                              "comment_id": 20,
                              "body": "<p>Question comment</p>",
                              "creation_date": 1772361100,
                              "owner": {
                                "display_name": "Bob"
                              }
                            }
                          ],
                          "has_more": false
                        }
                        """, APPLICATION_JSON));

        server.expect(once(), requestTo(startsWith("https://api.stackexchange.com/2.3/questions/123/answers?")))
                .andRespond(withSuccess("""
                        {
                          "items": [
                            {
                              "answer_id": 10,
                              "creation_date": 1772361000,
                              "last_activity_date": 1772361200,
                              "owner": {
                                "display_name": "Alice"
                              }
                            }
                          ],
                          "has_more": false
                        }
                        """, APPLICATION_JSON));

        server.expect(once(), requestTo(startsWith("https://api.stackexchange.com/2.3/answers/10/comments?")))
                .andRespond(withSuccess("""
                        {
                          "items": [
                            {
                              "comment_id": 30,
                              "body": "<p>Answer comment</p>",
                              "creation_date": 1772361200,
                              "owner": {
                                "display_name": "Charlie"
                              }
                            }
                          ],
                          "has_more": false
                        }
                        """, APPLICATION_JSON));

        List<LinkUpdateEvent> result = client.getUpdates(123L, since);

        assertThat(result)
                .extracting(LinkUpdateEvent::type, LinkUpdateEvent::title, LinkUpdateEvent::username)
                .containsExactly(
                        tuple(LinkUpdateType.STACKOVERFLOW_ANSWER, "Question title", "Alice"),
                        tuple(LinkUpdateType.STACKOVERFLOW_COMMENT, "Question title", "Bob"),
                        tuple(LinkUpdateType.STACKOVERFLOW_COMMENT, "Question title", "Charlie"));
    }

    @Test
    void shouldThrowWhenStackoverflowReturnsErrorStatus() {
        StackOverFlowHttpClient client = new StackOverFlowHttpClient(builder);

        server.expect(requestTo("https://api.stackexchange.com/2.3/questions/123?site=stackoverflow"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.getLastUpdate(123L))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("StackOverflow API returned status");
    }

    @Test
    void shouldThrowWhenStackoverflowReturnsInvalidBody() {
        StackOverFlowHttpClient client = new StackOverFlowHttpClient(builder);

        server.expect(requestTo("https://api.stackexchange.com/2.3/questions/123?site=stackoverflow"))
                .andRespond(withSuccess("{\"items\": []}", APPLICATION_JSON));

        assertThatThrownBy(() -> client.getLastUpdate(123L))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("invalid body");
    }
}
