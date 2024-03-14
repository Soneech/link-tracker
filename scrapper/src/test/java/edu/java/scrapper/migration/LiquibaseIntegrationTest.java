package edu.java.scrapper.migration;

import edu.java.scrapper.IntegrationEnvironment;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class LiquibaseIntegrationTest extends IntegrationEnvironment {
    private static String expectedUrl = "https://github.com/danvega/jwt-auth-demo";

    private static Long expectedChatId = 123456789L;

    private static OffsetDateTime expectedRegisteredAt = OffsetDateTime.now();

    @Test
    public void testConnection() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getUsername()).isEqualTo("postgres");
        assertThat(postgres.getPassword()).isEqualTo("postgres");
        assertThat(postgres.getDatabaseName()).isEqualTo("scrapper");
    }

    @Test
    public void testScrapperDBTables() {
        jdbcTemplate.update("INSERT INTO Link (url) VALUES (?)", expectedUrl);
        jdbcTemplate.update("INSERT INTO Chat (id, registered_at) VALUES (?, ?)",
            expectedChatId, expectedRegisteredAt);

        Long actualLinkId = jdbcTemplate
            .queryForObject("SELECT id FROM Link WHERE url = ?", Long.class, expectedUrl);
        jdbcTemplate.update("INSERT INTO Chat_Link (chat_id, link_id) VALUES(?, ?)", expectedChatId, actualLinkId);

        Long actualChatId = jdbcTemplate
            .queryForObject("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, actualLinkId);
        assertThat(actualChatId).isEqualTo(expectedChatId);
    }
}
