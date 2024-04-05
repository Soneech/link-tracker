package edu.java.bot.configuration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@EnableRetry
public record ApplicationConfig(
    @NotBlank
    String telegramToken,

    @NotNull
    Kafka kafka

) {
    public record Kafka(
        @NotBlank
        String bootstrapServers,

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
            String name,
            @NotBlank
            String consumerGroupId
        ) {
        }

        public record DlqTopic(
            @NotBlank
            String name
        ) {
        }
    }
}
