package edu.java.scrapper.dao.jdbc;

import edu.java.scrapper.IntegrationEnvironment;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcDaoTest extends IntegrationEnvironment {

    protected static JdbcTemplate jdbcTemplate;

    @BeforeAll
    public static void jdbcTemplateSetUp() {
        jdbcTemplate = new JdbcTemplate(DataSourceBuilder.create()
            .url(postgres.getJdbcUrl())
            .username(postgres.getUsername())
            .password(postgres.getPassword())
            .build());
    }
}
