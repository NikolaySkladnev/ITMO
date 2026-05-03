package backend.academy.linktracker.scrapper.support;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class AbstractPostgresJdbcIntegrationTest extends AbstractPostgresContainerTest {}
