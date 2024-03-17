package edu.java.client;

import edu.java.dto.stackoverflow.QuestionResponse;

public interface StackOverflowClient extends HttpClient {
    QuestionResponse fetchQuestion(Long questionId);

    QuestionResponse fetchQuestionAnswers(Long question);
}
