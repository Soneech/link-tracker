package edu.java.scrapper.service.updater.github.event;

import edu.java.dto.github.response.EventResponse;
import edu.java.dto.update.Update;
import edu.java.service.updater.github.event.PushEventHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

public class PushEventHandlerTest {

    private static PushEventHandler pushEventHandler;

    private static OffsetDateTime firstDateTime;

    private static String firstPrettyDateTime;

    private static OffsetDateTime secondDateTime;

    private static List<EventResponse> allEvents;

    private static List<EventResponse> pushEvents;

    private static final String PRETTY_DATE_TIME_PATTERN = "%s, %s UTC";

    private static final String EVENT_TYPE_NAME = "PushEvent";

    private static final String NEW_ONE_COMMIT_MESSAGE = "Новый коммит от пользователя %s  (создан в %s)\n";

    private static final String NEW_SEVERAL_COMMITS_MESSAGE =
        "Новых коммитов: %d от пользователя %s (последний создан в %s)";

    private static final String TEST_AUTHOR = "Aboba";

    @BeforeAll
    public static void setUp() {
        pushEventHandler = new PushEventHandler();

        firstDateTime =
            OffsetDateTime.of(2024, 3, 25, 15, 0, 0, 0, ZoneOffset.UTC);
        firstPrettyDateTime = PRETTY_DATE_TIME_PATTERN.formatted(firstDateTime.toLocalTime(), firstDateTime.toLocalDate());

        secondDateTime =
            OffsetDateTime.of(2024, 2, 20, 10, 0, 0, 0, ZoneOffset.UTC);

        allEvents = List.of(
            new EventResponse(EVENT_TYPE_NAME, new EventResponse.Actor(TEST_AUTHOR), firstDateTime, null),
            new EventResponse(EVENT_TYPE_NAME, new EventResponse.Actor(TEST_AUTHOR), secondDateTime, null),
            new EventResponse("PullRequestEvent", new EventResponse.Actor(TEST_AUTHOR), secondDateTime, null)
        );
        pushEvents = List.of(allEvents.getFirst(), allEvents.get(1));
    }

    @Test
    public void testGetEventType() {
        assertThat(pushEventHandler.getEventTypeName()).isEqualTo(EVENT_TYPE_NAME);
    }

    @Test
    public void testGetPrettyDateTime() {
        String actualPrettyDateTime = pushEventHandler.getPrettyDateTime(firstDateTime);
        assertThat(actualPrettyDateTime).isNotBlank().isEqualTo(firstPrettyDateTime);
    }

    @Test
    public void testGetAuthorsAndCommits() {
        Map<String, List<EventResponse>> authorsAndCommits = Map.of(
            pushEvents.getFirst().actor().login(), List.of(pushEvents.getFirst(), pushEvents.getLast())
        );

        var actualAuthorsAndCommits = pushEventHandler.getAuthorsAndCommits(pushEvents);
        assertThat(actualAuthorsAndCommits).isNotEmpty().isEqualTo(authorsAndCommits);
    }

    @Test
    public void testGetDescriptionForOneCommitUpdate() {
        String oneCommitMessage = NEW_ONE_COMMIT_MESSAGE.formatted(TEST_AUTHOR, firstPrettyDateTime);

        String actualOneCommitMessage = pushEventHandler.getDescriptionForOneCommitUpdate(TEST_AUTHOR,
            firstDateTime
        );
        assertThat(actualOneCommitMessage).isNotBlank().isEqualTo(oneCommitMessage);
    }

    @Test
    public void testGetDescriptionForSeveralCommitsUpdate() {
        int commitsCount = 2;
        String severalCommitsMessage = NEW_SEVERAL_COMMITS_MESSAGE
            .formatted(commitsCount, TEST_AUTHOR, firstPrettyDateTime);

        String actualSeveralCommitsMessage =
            pushEventHandler.getDescriptionForSeveralCommitsUpdate(TEST_AUTHOR, commitsCount, firstDateTime);

        assertThat(actualSeveralCommitsMessage).isNotBlank().isEqualTo(severalCommitsMessage);
    }

    @Test
    public void testGetCommitsLastUpdateTime() {
        OffsetDateTime currentLastUpdateTime =
            OffsetDateTime.of(2024, 1, 3, 14, 0, 0, 0, ZoneOffset.UTC);

        var expectedLastUpdateTime = firstDateTime;
        var actualLastUpdateTime = pushEventHandler.getCommitsLastUpdateTime(pushEvents, currentLastUpdateTime);

        assertThat(actualLastUpdateTime).isNotNull().isEqualTo(expectedLastUpdateTime);
    }

    @Test
    public void testFetchUpdatesWhenNoNew() {
        Optional<Update> actualUpdate = pushEventHandler.fetchUpdate(Collections.emptyList());
        assertThat(actualUpdate).isEmpty();
    }

    @Test
    public void testFetchUpdateWithOneCommit() {
        Optional<Update> expectedUpdate = Optional.of(new Update(
                NEW_ONE_COMMIT_MESSAGE.formatted(pushEvents.getFirst().actor().login(), firstPrettyDateTime),
                pushEvents.getFirst().createdAt()
            )
        );

        Optional<Update> actualUpdate = pushEventHandler.fetchUpdate(List.of(pushEvents.getFirst()));
        assertThat(actualUpdate).isNotEmpty().isEqualTo(expectedUpdate);
    }

    @Test
    public void testFetchUpdateWithSeveralCommits() {
        Optional<Update> expectedUpdate = Optional.of(
            new Update(
                NEW_SEVERAL_COMMITS_MESSAGE.formatted(pushEvents.size(), TEST_AUTHOR, firstPrettyDateTime),
                pushEvents.getFirst().createdAt()
            )
        );

        Optional<Update> actualUpdate = pushEventHandler.fetchUpdate(pushEvents);
        assertThat(actualUpdate).isNotEmpty().isEqualTo(expectedUpdate);
    }
}
