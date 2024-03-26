package edu.java.scrapper.liquibase;

import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class LiquibaseIntegrationTest extends IntegrationEnvironment {

    @Test
    public void testConnection() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getUsername()).isEqualTo("postgres");
        assertThat(postgres.getPassword()).isEqualTo("postgres");
        assertThat(postgres.getDatabaseName()).isEqualTo("scrapper");
    }
}
