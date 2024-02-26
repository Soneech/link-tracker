package edu.java.client;

import edu.java.dto.github.RepositoryResponse;

public interface GitHubClient extends HttpClient {
    RepositoryResponse fetchRepository(String user, String repository);
}
