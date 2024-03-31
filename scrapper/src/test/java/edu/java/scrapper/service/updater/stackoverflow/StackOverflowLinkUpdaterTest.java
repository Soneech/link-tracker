package edu.java.scrapper.service.updater.stackoverflow;

import edu.java.client.StackOverflowClient;
import edu.java.dto.stackoverflow.QuestionResponse;
import edu.java.dto.update.LinkUpdates;
import edu.java.dto.update.Update;
import edu.java.exception.stackoverflow.QuestionNotExistsException;
import edu.java.model.Link;
import edu.java.service.updater.stackoverflow.StackOverflowLinkUpdater;
import edu.java.service.updater.stackoverflow.event.AnswerEventHandler;
import edu.java.service.updater.stackoverflow.event.StackOverflowEventHandler;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StackOverflowLinkUpdaterTest {
    @Mock
    private StackOverflowClient stackOverflowWebClient;

    @Spy
    private ArrayList<StackOverflowEventHandler> eventHandlers;

    @Mock
    private AnswerEventHandler answerEventHandler;

    @InjectMocks
    private StackOverflowLinkUpdater linkUpdater;

    private static QuestionResponse questionResponse;

    private static Update update;

    private static Link link;

    private static final Long QUESTION_ID = 28295625L;

    private static final String QUESTION_NOT_EXISTS_MESSAGE =
        "Вопрос больше не существует :(\nСсылка будет удалена";

    private static final String SUPPORT_DOMAIN = "stackoverflow.com";

    private static final String TEST_URL = "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";


    @BeforeAll
    public static void setUp() {
        questionResponse = new QuestionResponse(List.of(
            new QuestionResponse.Item(List.of("java", "spring", "spring security")))
        );

        link = Link.builder().id(123456L).url(TEST_URL)
            .lastUpdateTime(OffsetDateTime.now())
            .lastCheckTime(OffsetDateTime.now()).build();

        update = new Update("some success description", OffsetDateTime.now());
    }

    @Test
    public void testSuccessFetchUpdates() {
        eventHandlers.add(answerEventHandler);
        when(stackOverflowWebClient.fetchQuestion(QUESTION_ID)).thenReturn(questionResponse);
        when(answerEventHandler.fetchUpdate(QUESTION_ID, link)).thenReturn(Optional.of(update));

        Optional<LinkUpdates> updates = linkUpdater.fetchUpdates(link);
        assertThat(updates).isPresent();
        assertThat(updates.get().getUpdates()).isNotEmpty().hasSize(1);
        assertThat(updates.get().getUpdates().getFirst()).isEqualTo(update);
        assertThat(updates.get().getLinkId()).isEqualTo(link.getId());
        assertThat(updates.get().getUrl()).isEqualTo(link.getUrl());
    }

    @Test
    public void testFailedFetchUpdates() {
        QuestionResponse response = new QuestionResponse(new ArrayList<>());

        when(stackOverflowWebClient.fetchQuestion(QUESTION_ID)).thenReturn(response);
        Optional<LinkUpdates> updates = linkUpdater.fetchUpdates(link);
        assertThat(updates).isPresent();
        assertThat(updates.get().getUpdates()).isNotEmpty().hasSize(1);
        assertThat(updates.get().getUpdates().getFirst().description()).isEqualTo(QUESTION_NOT_EXISTS_MESSAGE);
    }

    @Test
    public void testNoNewUpdates() {
        when(stackOverflowWebClient.fetchQuestion(QUESTION_ID)).thenReturn(questionResponse);

        Optional<LinkUpdates> updates = linkUpdater.fetchUpdates(link);
        assertThat(updates).isEmpty();
    }

    @Test
    public void testGetSupportDomain() {
        assertThat(linkUpdater.getSupportDomain()).isEqualTo(SUPPORT_DOMAIN);
    }

    @Test
    public void testSuccessCheckThatLinkExists() {
        when(stackOverflowWebClient.fetchQuestion(QUESTION_ID)).thenReturn(questionResponse);
        linkUpdater.checkThatLinkExists(link);
        verify(stackOverflowWebClient).fetchQuestion(QUESTION_ID);
    }

    @Test
    public void testFailedCheckThatLinkExists() {
        QuestionResponse response = new QuestionResponse(new ArrayList<>());

        when(stackOverflowWebClient.fetchQuestion(QUESTION_ID)).thenReturn(response);
        assertThatExceptionOfType(QuestionNotExistsException.class)
            .isThrownBy(() -> linkUpdater.checkThatLinkExists(link));
    }

    @Test
    public void testGetQuestionId() {
        assertThat(linkUpdater.getQuestionId(TEST_URL)).isEqualTo(28295625);
    }
}
