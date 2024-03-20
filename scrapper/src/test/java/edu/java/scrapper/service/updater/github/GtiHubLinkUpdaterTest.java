package edu.java.scrapper.service.updater.github;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.service.updater.github.GitHubLinkUpdater;
import edu.java.service.updater.github.event.GitHubEventHandler;
import edu.java.service.updater.github.event.PushEventHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GtiHubLinkUpdaterTest {

    @Mock
    private GitHubClient gitHubWebClient;

    @Spy
    private ArrayList<GitHubEventHandler> eventHandlers;

    @Mock
    private PushEventHandler pushEventHandler;

    @InjectMocks
    private GitHubLinkUpdater linkUpdater;

    private List<EventResponse> events;

    private static final String SUPPORT_DOMAIN = "github.com";

    private static final String TEST_URL = "https://github.com/graphql/graphiql";

    private static final String TEST_EVENT_TYPE = "PushEvent";

    private static final String USERNAME = "graphql";

    private static final String REPOSITORY = "graphiql";

    private static final String author = "acao";

    private static final String REPOSITORY_NOT_FOUND_MESSAGE =
        "Репозиторий больше не существует, либо стал приватным :(\nСсылка будет удалена.";

    @BeforeEach
    public void setUp() {
        events = List.of(
            EventResponse.builder()
                .type(TEST_EVENT_TYPE).actor(new EventResponse.Actor(author))
                .createdAt(OffsetDateTime.now()).build(),
            EventResponse.builder()
                .type(TEST_EVENT_TYPE).actor(new EventResponse.Actor(author))
                .createdAt(OffsetDateTime.of(2024, 3, 10, 14, 0, 0, 0, ZoneOffset.UTC)).build(),
            EventResponse.builder()
                .type("WatchEvent").actor(new EventResponse.Actor("some-author"))
                .createdAt(OffsetDateTime.now()).build()
        );

        eventHandlers.add(pushEventHandler);
    }

    @Test
    public void testGetSupportDomain() {
        assertThat(linkUpdater.getSupportDomain()).isEqualTo(SUPPORT_DOMAIN);
    }

    @Test
    public void testGetUserAndRepository() {
        Pair<String, String> userAndRepository = linkUpdater.getUserAndRepository(TEST_URL);
        assertThat(userAndRepository).isNotNull();
        assertThat(userAndRepository.getKey()).isEqualTo(USERNAME);
        assertThat(userAndRepository.getValue()).isEqualTo(REPOSITORY);
    }

    @Test
    public void testFetchEventsAndGetNew() {
        OffsetDateTime lastUpdateTime =
            OffsetDateTime.of(2024, 3, 15, 14, 0, 0, 0, ZoneOffset.UTC);
        when(gitHubWebClient.fetchRepositoryEvents(USERNAME, REPOSITORY)).thenReturn(events);
        List<EventResponse> newEvents = linkUpdater.fetchEventsAndGetNew(USERNAME, REPOSITORY, lastUpdateTime);

        assertThat(newEvents).isNotEmpty().hasSize(2);
        assertThat(newEvents.getFirst()).isEqualTo(events.getFirst());
    }
}
