package edu.java.client;

import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.exception.ResourceUnavailableException;
import java.util.List;

public interface GitHubClient extends HttpClient {
    RepositoryInfoResponse checkThatRepositoryExists(String user, String repository);

    List<EventResponse> fetchRepositoryEvents(String user, String repository);

    List<EventResponse> recoverFetchRepositoryEvents(ResourceUnavailableException exception,
        String user, String repository);
}
