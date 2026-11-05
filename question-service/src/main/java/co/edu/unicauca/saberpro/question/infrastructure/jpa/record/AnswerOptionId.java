package co.edu.unicauca.saberpro.question.infrastructure.jpa.record;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class AnswerOptionId implements Serializable {
    @Column(name = "question_id")
    private UUID questionId;

    @Column(name = "option_id", columnDefinition = "varchar")
    private String optionId;

    protected AnswerOptionId() {}

    public AnswerOptionId(UUID questionId, String optionId) {
        this.questionId = questionId;
        this.optionId = optionId;
    }

    public String getOptionId() { return optionId; }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AnswerOptionId that)) return false;
        return Objects.equals(questionId, that.questionId) && Objects.equals(optionId, that.optionId);
    }

    @Override
    public int hashCode() { return Objects.hash(questionId, optionId); }
}
