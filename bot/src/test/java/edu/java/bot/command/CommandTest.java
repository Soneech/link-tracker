package edu.java.bot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public abstract class CommandTest {

    @Mock
    protected Update update;

    @Mock
    protected Message message;

    @Mock
    protected Chat chat;

    protected Long chatId;

    @BeforeEach
    public void setUp() {
        chatId = 777L;

        lenient().doReturn(message).when(update).message();
        lenient().doReturn(chat).when(message).chat();
        lenient().doReturn(chatId).when(chat).id();
    }

    abstract void testThatReturnedCommandTypeIsCorrect();
    abstract void testThatReturnedCommandDescriptionIsCorrect();
}
