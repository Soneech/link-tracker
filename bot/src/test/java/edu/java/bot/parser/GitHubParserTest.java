package edu.java.bot.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class GitHubParserTest extends LinkParserTest {
    private GitHubParser gitHubParser;

    private static final String FIRST_GIT_HUB_URL = "https://github.com/pengrad/java-telegram-bot-api";

    private static final String SECOND_GIT_HUB_URL = "https://github.com/tonsky/FiraCode";

    private static final String FIRST_RANDOM_URI = "https://fintech.tinkoff.ru/study/";

    private static final String SECOND_RANDOM_URL = "https://stackoverflow.com/questions/28295625/mockito-spy-vs-mock";

    @BeforeEach
    public void setUp() {
        gitHubParser = new GitHubParser();
    }

    @Test
    @Override
    public void testWithSupportedWebserviceLink() {
        assertThat(gitHubParser.isLinkCorrect(FIRST_GIT_HUB_URL)).isTrue();
        assertThat(gitHubParser.isLinkCorrect(SECOND_GIT_HUB_URL)).isTrue();
    }

    @Test
    @Override
    public void testWithUnsupportedWebserviceLink() {
        assertThat(gitHubParser.isLinkCorrect(FIRST_RANDOM_URI)).isFalse();
        assertThat(gitHubParser.isLinkCorrect(SECOND_RANDOM_URL)).isFalse();
    }
}
