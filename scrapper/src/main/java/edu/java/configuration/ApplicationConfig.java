package edu.java.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
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
    String databaseAccessType,

    boolean useQueue

) {
    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) { }

    public record Kafka(
        @NotEmpty
        List<String> bootstrapServers,

        @NotBlank
        String typeMappings,

        @NotBlank
        String trustedPackages,

        @NotNull
        LinkUpdatesTopic linkUpdatesTopic,

        @NotNull
        DlqTopic dlqTopic

    ) {
        public record LinkUpdatesTopic(
           @NotBlank
           String name
        ) {
        }

        public record DlqTopic(
            @NotBlank
            String name,

            @NotBlank
            String consumerGroupId
        ) {
        }
    }
}
