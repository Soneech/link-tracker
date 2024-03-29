package edu.java.client;

import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.github.RepositoryNotExistsException;
import java.util.List;
import org.springframework.retry.annotation.Recover;

public interface GitHubClient extends HttpClient {
    RepositoryInfoResponse checkThatRepositoryExists(String user, String repository);

    List<EventResponse> fetchRepositoryEvents(String user, String repository);

    @Recover
    default RepositoryInfoResponse recoverCheckThatRepositoryExists(RepositoryNotExistsException exception,
        String user, String repository) {
        throw exception;
    }

    @Recover
    default List<EventResponse> recoverFetchRepositoryEvents(RepositoryNotExistsException exception,
        String user, String repository) {
        throw exception;
    }
}
