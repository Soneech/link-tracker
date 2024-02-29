package edu.java.bot.command.chain.impl.track;

import edu.java.bot.command.chain.Result;
import org.junit.jupiter.api.BeforeEach;

public abstract class TrackCommandStepTest {

    protected Long chatId;

    protected Result result;

    protected String[] messageParts;

    @BeforeEach
    public void setUp() {
        chatId = 777L;
    }
}
