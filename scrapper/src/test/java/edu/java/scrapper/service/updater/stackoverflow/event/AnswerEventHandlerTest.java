package edu.java.scrapper.service.updater.stackoverflow.event;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.AnswersResponse;
import edu.java.dto.update.Update;
import edu.java.exception.ResourceUnavailableException;
import edu.java.model.Chat;
import edu.java.model.Link;
import edu.java.service.updater.stackoverflow.event.AnswerEventHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnswerEventHandlerTest {

    @Mock
    private StackOverflowClient stackOverflowWebClient;

    @InjectMocks
    private AnswerEventHandler answerEventHandler;

    private static Link testLink;

    private static AnswersResponse answersResponse;

    private static final String NEW_ANSWER_MESSAGE = "Новый ответ от пользователя %s";

    private static final String UPDATE_ANSWER_MESSAGE = "Изменения в ответе от пользователя %s";

    private static final String TEST_AUTHOR = "Aboba";

    private static final long TEST_QUESTION_ID = 819489441;

    @BeforeAll
    public static void setUp() {
        OffsetDateTime linkLastCheckTime =
            OffsetDateTime.of(2024, 4, 15, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime linkLastUpdateTime =
            OffsetDateTime.of(2024, 4, 14, 10, 0, 0, 0, ZoneOffset.UTC);

        testLink = Link.builder()
            .id(TEST_QUESTION_ID)
            .url("https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock")
            .lastUpdateTime(linkLastUpdateTime)
            .lastCheckTime(linkLastCheckTime)
            .tgChats(List.of(new Chat(123L), new Chat(456L)))
            .build();

        OffsetDateTime answerCreationDateTime =
            OffsetDateTime.of(2024, 4, 16, 9, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime answerLastActivityDateTime =
            OffsetDateTime.of(2024, 4, 16, 15, 0, 0, 0, ZoneOffset.UTC);

        answersResponse = new AnswersResponse(List.of(
            new AnswersResponse.AnswerResponse(
                new AnswersResponse.AnswerResponse.Owner(TEST_AUTHOR),
                answerLastActivityDateTime, answerCreationDateTime,
                151894L
            )
        ));
    }

    @Test
    public void testGetLatestUpdateTime() {
        OffsetDateTime oldDateTime =
            OffsetDateTime.of(2024, 1, 15, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime newDateTime =
            OffsetDateTime.of(2024, 2, 15, 10, 0, 0, 0, ZoneOffset.UTC);

        var latestDateTime = answerEventHandler.getLatestUpdateTime(oldDateTime, newDateTime);
        assertThat(latestDateTime).isEqualTo(newDateTime);
    }

    @Test
    public void testAddNewUpdateDescription() {
        StringBuilder descriptions = new StringBuilder();
        String newAnswerMessage = NEW_ANSWER_MESSAGE.formatted(TEST_AUTHOR) + "\n";
        String newUpdateAnswerMessage = UPDATE_ANSWER_MESSAGE.formatted(TEST_AUTHOR) + "\n";

        answerEventHandler.addNewUpdateDescription(descriptions, NEW_ANSWER_MESSAGE, TEST_AUTHOR);
        assertThat(descriptions.toString()).isEqualTo(newAnswerMessage);

        answerEventHandler.addNewUpdateDescription(descriptions, UPDATE_ANSWER_MESSAGE, TEST_AUTHOR);
        assertThat(descriptions.toString()).isEqualTo(newAnswerMessage + newUpdateAnswerMessage);
    }

    @Test
    public void testFetchUpdateWhenStackOverflowIsUnavailable() {
        when(stackOverflowWebClient.fetchQuestionAnswers(TEST_QUESTION_ID))
            .thenThrow(ResourceUnavailableException.class);

        Optional<Update> actualUpdate = answerEventHandler.fetchUpdate(TEST_QUESTION_ID, testLink);
        assertThat(actualUpdate).isEmpty();
    }

    @Test
    public void testFetchUpdateWithNewAnswer() {
        when(stackOverflowWebClient.fetchQuestionAnswers(TEST_QUESTION_ID))
            .thenReturn(answersResponse);

        Optional<Update> expectedUpdate =
            Optional.of(new Update(
                NEW_ANSWER_MESSAGE.formatted(TEST_AUTHOR) + "\n",
                answersResponse.items().getFirst().creationDate())
            );
        Optional<Update> actualUpdate = answerEventHandler.fetchUpdate(TEST_QUESTION_ID, testLink);

        assertThat(actualUpdate).isPresent().isEqualTo(expectedUpdate);
    }

    @Test
    public void testFetchUpdateWithAnswerChanges() {
        when(stackOverflowWebClient.fetchQuestionAnswers(TEST_QUESTION_ID))
            .thenReturn(answersResponse);

        Link newLink = Link.builder()
            .id(testLink.getId())
            .url(testLink.getUrl())
            .lastCheckTime(testLink.getLastCheckTime())
            .lastUpdateTime(answersResponse.items().getFirst().creationDate())
            .build();

        Optional<Update> expectedUpdate =
            Optional.of(new Update(
                UPDATE_ANSWER_MESSAGE.formatted(TEST_AUTHOR) + "\n",
                answersResponse.items().getFirst().lastActivityDate())
            );

        Optional<Update> actualUpdate = answerEventHandler.fetchUpdate(TEST_QUESTION_ID, newLink);

        assertThat(actualUpdate).isPresent().isEqualTo(expectedUpdate);
    }
}
