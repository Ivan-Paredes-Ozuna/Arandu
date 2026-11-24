package com.arandu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class HelpDtos {

    private HelpDtos() {
    }

    public record HelpTopicResponse(Long id, String question, String answer, String tags) {
    }

    public record HelpSearchResponse(List<HelpTopicResponse> similarDoubts, String contextualHint) {
    }

    public record TutorQuestionRequest(
            @NotNull Long courseId,
            Long activityId,
            @NotBlank(message = "Escribí tu consulta") String question,
            boolean privateQuestion) {
    }

    public record TutorQuestionResponse(
            Long id,
            String question,
            boolean privateQuestion,
            String status,
            Instant createdAt) {
    }
}
