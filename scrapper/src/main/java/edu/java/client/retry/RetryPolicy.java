package edu.java.client.retry;

import io.github.resilience4j.retry.Retry;
import java.util.List;
import org.springframework.http.HttpStatus;

public interface RetryPolicy {
    String getTypeName();

    Retry setUpRetry(List<HttpStatus> statusCodes);
}
