package backend.academy.linktracker.scrapper.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import backend.academy.linktracker.scrapper.ScrapperApplication;
import backend.academy.linktracker.scrapper.application.dto.ListLinkResult;
import backend.academy.linktracker.scrapper.application.port.in.AddLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.in.CheckUpdatesUseCase;
import backend.academy.linktracker.scrapper.application.port.in.DeleteChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.GetAllLinksUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RegisterChatUseCase;
import backend.academy.linktracker.scrapper.application.port.in.RemoveLinkUseCase;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import backend.academy.linktracker.scrapper.application.port.out.GitHubClient;
import backend.academy.linktracker.scrapper.application.port.out.StackOverFlowClient;
import backend.academy.linktracker.scrapper.domain.entities.Chat;
import backend.academy.linktracker.scrapper.domain.entities.Link;
import backend.academy.linktracker.scrapper.support.AbstractPostgresContainerTest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(
        classes = ScrapperApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
            "spring.task.scheduling.enabled=false",
            "spring.grpc.server.port=0",
            "app.scheduler.interval=99999999",
            "app.github.token=test-token",
            "app.stackoverflow.key=test-key",
            "app.stackoverflow.access-token=test-access-token",
            "app.bot.transport=rest",
            "app.bot.rest.base-url=http://localhost:65535"
        })
@Import(AbstractDatabaseIntegrationTest.TestConfig.class)
public abstract class AbstractDatabaseIntegrationTest extends AbstractPostgresContainerTest {

    protected static final long CHAT_ID = 123L;
    protected static final String URL = "https://github.com/openai/openai-java";
    protected static final List<String> TAGS = List.of("java", "backend");
    protected static final Instant FIRST_UPDATE_TIME = Instant.parse("2024-01-01T10:00:00Z");
    protected static final Instant SECOND_UPDATE_TIME = Instant.parse("2024-01-02T12:00:00Z");

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected JdbcClient jdbcClient;

    @Autowired
    protected ChatDataRepository chatDataRepository;

    @Autowired
    protected RegisterChatUseCase registerChatUseCase;

    @Autowired
    protected DeleteChatUseCase deleteChatUseCase;

    @Autowired
    protected AddLinkUseCase addLinkUseCase;

    @Autowired
    protected RemoveLinkUseCase removeLinkUseCase;

    @Autowired
    protected GetAllLinksUseCase getAllLinksUseCase;

    protected abstract Class<? extends ChatDataRepository> expectedRepositoryType();

    @BeforeEach
    void setUp() {
        if (businessTablesExist()) {
            jdbcClient.sql("""
                    TRUNCATE TABLE subscription_tags, subscriptions, tags, links, chats
                    RESTART IDENTITY CASCADE
                    """).update();
        }
    }

    private boolean businessTablesExist() {
        Integer count = jdbcClient.sql("""
                    select count(*)
                    from information_schema.tables
                    where table_schema = 'public'
                      and table_name in ('chats', 'links', 'subscriptions', 'tags', 'subscription_tags')
                    """).query(Integer.class).single();

        return count != null && count == 5;
    }

    @Test
    void contextStartsAndLiquibaseAppliesMigrations() {
        Integer changelogCount = jdbcClient
                .sql("select count(*) from databasechangelog where id = '001_init-schema'")
                .query(Integer.class)
                .single();

        Integer tablesCount = jdbcClient.sql("""
                    select count(*)
                    from information_schema.tables
                    where table_schema = 'public'
                      and table_name in ('chats', 'links', 'subscriptions', 'tags', 'subscription_tags')
                    """).query(Integer.class).single();

        assertThat(changelogCount).isEqualTo(1);
        assertThat(tablesCount).isEqualTo(5);
    }

    @Test
    void correctRepositoryImplementationIsSelectedByAccessType() {
        ChatDataRepository repository = applicationContext.getBean(ChatDataRepository.class);
        assertThat(repository).isInstanceOf(expectedRepositoryType());
    }

    @Test
    void registerChatPersistsChatInDatabase() {
        registerChatUseCase.registerChat(CHAT_ID);

        assertThat(chatDataRepository.hasChat(new Chat(CHAT_ID))).isTrue();

        Integer count = jdbcClient
                .sql("select count(*) from chats where chat_id = :chatId")
                .param("chatId", CHAT_ID)
                .query(Integer.class)
                .single();

        assertThat(count).isEqualTo(1);
    }

    @Test
    void addLinkPersistsSubscriptionTagsAndLastUpdatedAt() {
        registerChatUseCase.registerChat(CHAT_ID);

        addLinkUseCase.addLink(CHAT_ID, URL, TAGS, List.of());

        Link savedLink =
                chatDataRepository.findLinkByChat(new Chat(CHAT_ID), URL).orElseThrow();
        Instant savedUpdateTime = chatDataRepository.getLastUpdateTime(new Link(URL, List.of()));
        ListLinkResult allLinks = getAllLinksUseCase.getAllLinks(CHAT_ID);

        assertThat(savedLink.url()).isEqualTo(URL);
        assertThat(savedLink.tags()).containsExactlyInAnyOrderElementsOf(TAGS);
        assertThat(savedUpdateTime).isEqualTo(FIRST_UPDATE_TIME);
        assertThat(allLinks.size()).isEqualTo(1);

        Integer subscriptionsCount = jdbcClient
                .sql("select count(*) from subscriptions")
                .query(Integer.class)
                .single();
        Integer tagsCount =
                jdbcClient.sql("select count(*) from tags").query(Integer.class).single();
        Integer linkTagsCount = jdbcClient
                .sql("select count(*) from subscription_tags")
                .query(Integer.class)
                .single();

        assertThat(subscriptionsCount).isEqualTo(1);
        assertThat(tagsCount).isEqualTo(2);
        assertThat(linkTagsCount).isEqualTo(2);
    }

    @Test
    void setLastUpdateTimeUpdatesExistingLink() {
        registerChatUseCase.registerChat(CHAT_ID);
        addLinkUseCase.addLink(CHAT_ID, URL, TAGS, List.of());

        chatDataRepository.setLastUpdateTime(new Link(URL, List.of()), SECOND_UPDATE_TIME);

        Instant actual = chatDataRepository.getLastUpdateTime(new Link(URL, List.of()));
        assertThat(actual).isEqualTo(SECOND_UPDATE_TIME);
    }

    @Test
    void removeLinkDeletesSubscriptionAndOrphanLink() {
        registerChatUseCase.registerChat(CHAT_ID);
        addLinkUseCase.addLink(CHAT_ID, URL, TAGS, List.of());

        removeLinkUseCase.removeLink(CHAT_ID, URL);

        assertThat(chatDataRepository.findLinkByChat(new Chat(CHAT_ID), URL)).isEmpty();

        Integer subscriptionsCount = jdbcClient
                .sql("select count(*) from subscriptions")
                .query(Integer.class)
                .single();
        Integer linksCount = jdbcClient
                .sql("select count(*) from links")
                .query(Integer.class)
                .single();
        Integer subscriptionTagsCount = jdbcClient
                .sql("select count(*) from subscription_tags")
                .query(Integer.class)
                .single();

        assertThat(subscriptionsCount).isZero();
        assertThat(linksCount).isZero();
        assertThat(subscriptionTagsCount).isZero();
    }

    @Test
    void deleteChatRemovesChatSubscriptionsAndOrphanLinks() {
        registerChatUseCase.registerChat(CHAT_ID);
        addLinkUseCase.addLink(CHAT_ID, URL, TAGS, List.of());

        deleteChatUseCase.deleteChat(CHAT_ID);

        assertThat(chatDataRepository.hasChat(new Chat(CHAT_ID))).isFalse();

        Integer chatsCount = jdbcClient
                .sql("select count(*) from chats")
                .query(Integer.class)
                .single();
        Integer subscriptionsCount = jdbcClient
                .sql("select count(*) from subscriptions")
                .query(Integer.class)
                .single();
        Integer linksCount = jdbcClient
                .sql("select count(*) from links")
                .query(Integer.class)
                .single();

        assertThat(chatsCount).isZero();
        assertThat(subscriptionsCount).isZero();
        assertThat(linksCount).isZero();
    }

    @Test
    void addingDuplicateLinkThrowsIllegalStateException() {
        registerChatUseCase.registerChat(CHAT_ID);
        addLinkUseCase.addLink(CHAT_ID, URL, TAGS, List.of());

        assertThatThrownBy(() -> addLinkUseCase.addLink(CHAT_ID, URL, TAGS, List.of()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already tracked");
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        GitHubClient gitHubClient() {
            return (_, _) -> FIRST_UPDATE_TIME;
        }

        @Bean
        @Primary
        StackOverFlowClient stackOverFlowClient() {
            return _ -> FIRST_UPDATE_TIME;
        }

        @Bean
        @Primary
        CheckUpdatesUseCase checkUpdatesUseCase() {
            return () -> {};
        }
    }
}
