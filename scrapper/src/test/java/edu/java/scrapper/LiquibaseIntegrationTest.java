package edu.java.scrapper;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class LiquibaseIntegrationTest extends IntegrationTest {
    private static String expectedUrl = "https://github.com/danvega/jwt-auth-demo";

    private static Long expectedChatId = 123456789L;

    private static String expectedChatName = "Soneech";

    @Test
    public void testConnection() {
        assertTrue(postgres.isRunning());
        assertThat(postgres.getUsername()).isEqualTo("postgres");
        assertThat(postgres.getPassword()).isEqualTo("postgres");
        assertThat(postgres.getDatabaseName()).isEqualTo("scrapper");
    }

    @Test
    public void testScrapperDBTables() {
        jdbcTemplate.update("INSERT INTO Link (url) VALUES (?)", expectedUrl);
        jdbcTemplate.update("INSERT INTO Chat (id, name) VALUES (?, ?)", expectedChatId, expectedChatName);

        String actualChatName = jdbcTemplate
            .queryForObject("SELECT name FROM Chat WHERE id = ?", String.class, expectedChatId);
        assertThat(actualChatName).isEqualTo(expectedChatName);

        Long actualLinkId = jdbcTemplate
            .queryForObject("SELECT id FROM Link WHERE url = ?", Long.class, expectedUrl);
        jdbcTemplate.update("INSERT INTO Chat_Link (chat_id, link_id) VALUES(?, ?)", expectedChatId, actualLinkId);

        Long actualChatId = jdbcTemplate
            .queryForObject("SELECT chat_id FROM chat_link WHERE link_id = ?", Long.class, actualLinkId);
        assertThat(actualChatId).isEqualTo(expectedChatId);
    }
}
