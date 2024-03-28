package edu.java.client;

import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import java.util.List;

public interface GitHubClient extends HttpClient {
    RepositoryInfoResponse checkThatRepositoryExists(String user, String repository);

    RepositoryInfoResponse checkThatRepositoryExistsWithRetry(String user, String repository);

    List<EventResponse> fetchRepositoryEvents(String user, String repository);

    List<EventResponse> fetchRepositoryEventsWithRetry(String user, String repository);
}
