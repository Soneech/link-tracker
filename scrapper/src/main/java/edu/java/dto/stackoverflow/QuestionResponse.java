package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.List;

public record QuestionResponse(List<AnswerResponse> items) {
    @Builder

    public record AnswerResponse(
        Owner owner,

        @JsonProperty("last_activity_date")
        OffsetDateTime lastActivityDate,

        @JsonProperty("creation_date")
        OffsetDateTime creationDate,

        @JsonProperty("question_id")
        Long id
    ) {
        public record Owner(
            @JsonProperty("display_name")
            String name
        ) { }
    }
}

