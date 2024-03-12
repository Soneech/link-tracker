package edu.java.bot.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertTrue(gitHubParser.isLinkCorrect(FIRST_GIT_HUB_URL));
        assertTrue(gitHubParser.isLinkCorrect(SECOND_GIT_HUB_URL));
    }

    @Test
    @Override
    public void testWithUnsupportedWebserviceLink() {
        assertFalse(gitHubParser.isLinkCorrect(FIRST_RANDOM_URI));
        assertFalse(gitHubParser.isLinkCorrect(SECOND_RANDOM_URL));
    }
}
