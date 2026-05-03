package backend.academy.linktracker.scrapper.db;

import backend.academy.linktracker.scrapper.adapter.out.db.orm.OrmChatDataRepository;
import backend.academy.linktracker.scrapper.application.port.out.ChatDataRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.database.access-type=orm")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrmDatabaseIntegrationTest extends AbstractDatabaseIntegrationTest {

    @Override
    protected Class<? extends ChatDataRepository> expectedRepositoryType() {
        return OrmChatDataRepository.class;
    }
}
