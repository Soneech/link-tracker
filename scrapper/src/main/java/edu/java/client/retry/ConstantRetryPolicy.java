package edu.java.client.retry;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@Setter
@Getter
@ConfigurationProperties("api.retry.constant")
public class ConstantRetryPolicy implements RetryPolicy {

    private String typeName;

    private int maxAttempts;

    private long intervalMillis;

    @Override
    public Retry setUpRetry(List<HttpStatus> statusCodes) {
        RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(maxAttempts)
            .intervalFunction(IntervalFunction.of(Duration.ofMillis(intervalMillis)))
            .retryOnException(exception -> exception instanceof WebClientResponseException
                && statusCodes.contains(((WebClientResponseException) exception).getStatusCode()))
            .build();
        return Retry.of(typeName, retryConfig);
    }
}
