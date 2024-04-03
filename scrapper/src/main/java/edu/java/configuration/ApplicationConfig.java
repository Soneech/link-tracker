package edu.java.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@EnableScheduling
@EnableRetry
public record ApplicationConfig(
    @NotNull
    @Bean
    Scheduler scheduler,

    @NotNull
    Kafka kafka,

    @NotBlank
    String databaseAccessType

) {
    public record Scheduler(
        boolean enable,

        @NotNull
        Duration interval,

        @NotNull
        Duration forceCheckDelay
    ) {
    }

    public record Kafka(
        @NotBlank
        String bootstrapServers,

        long lingerMsConfig,

        @NotBlank
        String keySerializer,

        @NotBlank
        String valueSerializer
    ){
    }
}
