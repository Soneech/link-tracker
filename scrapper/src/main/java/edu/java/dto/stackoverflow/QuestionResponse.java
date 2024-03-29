package edu.java.dto.stackoverflow;

import java.util.List;

public record QuestionResponse(List<Item> items) {
    public record Item(List<String> tags) {
    }
}
