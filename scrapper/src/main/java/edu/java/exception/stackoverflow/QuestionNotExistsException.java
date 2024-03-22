package edu.java.exception.stackoverflow;

import edu.java.exception.ResourceNotExistsException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionNotExistsException extends ResourceNotExistsException {

    private final String message;

    public QuestionNotExistsException(long questionId) {
        this.message = "Question with id: %d not exists".formatted(questionId);
    }
}
