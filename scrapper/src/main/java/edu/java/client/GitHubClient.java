package edu.java.client;

import edu.java.dto.github.RepositoryPushEventResponse;

public interface GitHubClient extends HttpClient {
    RepositoryPushEventResponse fetchRepositoryPushEvent(String user, String repository);
}
