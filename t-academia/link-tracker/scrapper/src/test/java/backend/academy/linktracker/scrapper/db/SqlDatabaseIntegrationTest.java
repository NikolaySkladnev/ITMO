package backend.academy.linktracker.scrapper.db;

import backend.academy.linktracker.scrapper.adapter.out.db.sql.SqlChatDataRepository;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
        properties = {
            "app.database.access-type=sql",
            "spring.jpa.hibernate.ddl-auto=none",
            "spring.data.jpa.repositories.enabled=false"
        })
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class SqlDatabaseIntegrationTest extends AbstractDatabaseIntegrationTest {

    @Override
    protected Class<? extends ChatDataRepository> expectedRepositoryType() {
        return SqlChatDataRepository.class;
    }
}
