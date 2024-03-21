package edu.java.scrapper.service.updater.github;

import edu.java.client.GitHubClient;
import edu.java.dto.github.response.EventResponse;
import edu.java.dto.github.response.RepositoryInfoResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.exception.github.RepositoryNotExistsException;
import edu.java.model.Link;
import edu.java.service.updater.github.GitHubLinkUpdater;
import edu.java.service.updater.github.event.GitHubEventHandler;
import edu.java.service.updater.github.event.PushEventHandler;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GtiHubLinkUpdaterTest {

    @Mock
    private GitHubClient gitHubWebClient;

    @Mock
    private PushEventHandler pushEventHandler;

    private GitHubLinkUpdater linkUpdater;

    private static List<EventResponse> events;

    private static List<EventResponse> newEvents;

    private static Update pushEventUpdate;

    private static Link testLink;

    private static OffsetDateTime oldLastUpdateTime;

    private static OffsetDateTime newLastUpdateTime;

    private static final String SUPPORT_DOMAIN = "github.com";

    private static final String TEST_URL = "https://github.com/graphql/graphiql";

    private static final String TEST_EVENT_TYPE = "PushEvent";

    private static final String USERNAME = "graphql";

    private static final String REPOSITORY = "graphiql";

    private static final Long REPOSITORY_ID = 40518025L;

    private static final String AUTHOR = "acao";

    private static final String REPOSITORY_NOT_FOUND_MESSAGE =
        "Репозиторий больше не существует, либо стал приватным :(\nСсылка будет удалена.";

    @BeforeAll
    public static void setUp() {
        events = List.of(
            EventResponse.builder()
                .type(TEST_EVENT_TYPE).actor(new EventResponse.Actor(AUTHOR))
                .createdAt(OffsetDateTime.of(2024, 3, 12, 14, 0, 0, 0, ZoneOffset.UTC)).build(),
            EventResponse.builder()
                .type(TEST_EVENT_TYPE).actor(new EventResponse.Actor(AUTHOR))
                .createdAt(OffsetDateTime.of(2024, 3, 10, 14, 0, 0, 0, ZoneOffset.UTC)).build(),
            EventResponse.builder()
                .type("WatchEvent").actor(new EventResponse.Actor("some-author"))
                .createdAt(OffsetDateTime.now()).build()
        );
        oldLastUpdateTime = events.get(1).createdAt();
        newLastUpdateTime = events.getFirst().createdAt();
        newEvents = List.of(events.getFirst(), events.getLast());

        pushEventUpdate = new Update("some description", events.getFirst().createdAt());

        testLink = Link.builder()
            .id(777888999L)
            .url(TEST_URL)
            .lastCheckTime(OffsetDateTime.of(2024, 3, 9, 14, 0, 0, 0, ZoneOffset.UTC))
            .lastUpdateTime(oldLastUpdateTime)
            .build();
    }

    @BeforeEach
    public void setUpUpdater() {
        when(pushEventHandler.getEventTypeName()).thenReturn(TEST_EVENT_TYPE);

        List<GitHubEventHandler> eventHandlers = List.of(pushEventHandler);
        linkUpdater = new GitHubLinkUpdater(gitHubWebClient, eventHandlers);
    }

    @Test
    public void testSuccessFetchUpdates() {
        LinkUpdates linkUpdates = LinkUpdates.builder()
            .linkId(testLink.getId())
            .url(testLink.getUrl())
            .httpStatus(HttpStatus.OK)
            .lastUpdateTime(newLastUpdateTime)
            .updates(List.of(pushEventUpdate))
            .tgChatIds(Collections.emptyList())
            .build();

        when(gitHubWebClient.fetchRepositoryEvents(USERNAME, REPOSITORY)).thenReturn(events);
        when(pushEventHandler.fetchUpdate(List.of(events.getFirst()))).thenReturn(Optional.of(pushEventUpdate));

        Optional<LinkUpdates> actualLinkUpdates = linkUpdater.fetchUpdates(testLink);
        assertThat(actualLinkUpdates).isPresent();
        assertThat(actualLinkUpdates.get()).isEqualTo(linkUpdates);
    }

    @Test
    public void testNoNewUpdates() {
        when(gitHubWebClient.fetchRepositoryEvents(USERNAME, REPOSITORY)).thenReturn(Collections.emptyList());
        Optional<LinkUpdates> actualLinkUpdates = linkUpdater.fetchUpdates(testLink);

        assertThat(actualLinkUpdates).isEmpty();
    }

    @Test
    public void testFailedFetchUpdates() {
        when(gitHubWebClient.fetchRepositoryEvents(USERNAME, REPOSITORY))
            .thenThrow(RepositoryNotExistsException.class);

        Optional<LinkUpdates> actualLinkUpdates = linkUpdater.fetchUpdates(testLink);

        assertThat(actualLinkUpdates).isPresent();
        assertThat(actualLinkUpdates.get().getUpdates().getFirst().description())
            .isEqualTo(REPOSITORY_NOT_FOUND_MESSAGE);
        assertThat(actualLinkUpdates.get().getLinkId()).isEqualTo(testLink.getId());
        assertThat(actualLinkUpdates.get().getUrl()).isEqualTo(testLink.getUrl());
        assertThat(actualLinkUpdates.get().getHttpStatus()).isEqualTo(HttpStatus.GONE);
    }

    @Test
    public void testGetSupportDomain() {
        assertThat(linkUpdater.getSupportDomain()).isEqualTo(SUPPORT_DOMAIN);
    }

    @Test
    public void testSuccessCheckLinkExistence() {
        RepositoryInfoResponse response =
            new RepositoryInfoResponse(REPOSITORY_ID, USERNAME + "/" + REPOSITORY);

        when(gitHubWebClient.checkThatRepositoryExists(USERNAME, REPOSITORY)).thenReturn(response);
        linkUpdater.checkThatLinkExists(testLink);
        verify(gitHubWebClient).checkThatRepositoryExists(USERNAME, REPOSITORY);
    }

    @Test
    public void testFailedCheckLinkExistence() {
        when(gitHubWebClient.checkThatRepositoryExists(USERNAME, REPOSITORY))
            .thenThrow(RepositoryNotExistsException.class);

        assertThatExceptionOfType(RepositoryNotExistsException.class)
            .isThrownBy(() -> linkUpdater.checkThatLinkExists(testLink));
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
        when(gitHubWebClient.fetchRepositoryEvents(USERNAME, REPOSITORY)).thenReturn(events);
        List<EventResponse> actualNewEvents = linkUpdater.fetchEventsAndGetNew(USERNAME, REPOSITORY, oldLastUpdateTime);

        assertThat(actualNewEvents).isNotEmpty().hasSize(2);
        assertThat(actualNewEvents).isEqualTo(newEvents);
    }

    @Test
    public void testFetchUpdateForEventType() {
        List<EventResponse> currentTypeEvents = List.of(newEvents.getFirst());

        when(pushEventHandler.fetchUpdate(currentTypeEvents)).thenReturn(Optional.of(pushEventUpdate));

        Optional<Update> update = linkUpdater.fetchUpdateForEventType(newEvents, TEST_EVENT_TYPE, pushEventHandler);
        verify(pushEventHandler).fetchUpdate(currentTypeEvents);
        assertThat(update).isPresent();
        assertThat(update.get().updateTime()).isEqualTo(currentTypeEvents.getFirst().createdAt());
    }
}
