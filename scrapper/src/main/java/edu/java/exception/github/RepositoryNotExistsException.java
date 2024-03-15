package edu.java.exception.github;

import edu.java.dto.github.GitHubErrorResponse;
import edu.java.exception.ResourceNotExistsException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class RepositoryNotExistsException extends ResourceNotExistsException {
    private final GitHubErrorResponse response;
}
