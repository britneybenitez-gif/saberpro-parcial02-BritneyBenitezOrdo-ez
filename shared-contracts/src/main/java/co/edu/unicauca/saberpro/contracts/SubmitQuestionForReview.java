package co.edu.unicauca.saberpro.contracts;

import java.util.Objects;
import java.util.UUID;

public record SubmitQuestionForReview(UUID operationId, UUID questionId) {
    public SubmitQuestionForReview {
        Objects.requireNonNull(operationId, "operationId");
        Objects.requireNonNull(questionId, "questionId");
    }
}
