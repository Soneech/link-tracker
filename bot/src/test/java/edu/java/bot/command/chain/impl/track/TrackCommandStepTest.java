package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.chain.Result;
import edu.java.bot.service.UserChatService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Spy;

public abstract class TrackCommandStepTest {
    @Spy
    protected UserChatService userChatService;

    protected Long chatId;

    protected Result result;

    protected String[] messageParts;

    @BeforeEach
    public void setUp() {
        chatId = 777L;
    }
}
