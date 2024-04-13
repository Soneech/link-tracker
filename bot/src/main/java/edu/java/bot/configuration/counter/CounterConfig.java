package edu.java.bot.configuration.counter;

import edu.java.bot.configuration.ApplicationConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CounterConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public Counter processedUpdatesCounter(ApplicationConfig config, MeterRegistry registry) {
        return Counter.builder(config.metrics().processedUpdates().name())
                .description(config.metrics().processedUpdates().description())
                .tag("application", applicationName)
                .register(registry);
    }
}
