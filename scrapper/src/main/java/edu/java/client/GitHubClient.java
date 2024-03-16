package edu.java.client;

import edu.java.dto.github.response.PullRequestEventResponse;
import edu.java.dto.github.response.PushEventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import java.util.List;

public interface GitHubClient extends HttpClient {
    RepositoryInfoResponse checkThatRepositoryExists(String user, String repository);

    PushEventResponse fetchRepositoryPushEvent(String user, String repository);

    List<PullRequestEventResponse> fetchRepositoryPullEvent(String user, String repository);
}
