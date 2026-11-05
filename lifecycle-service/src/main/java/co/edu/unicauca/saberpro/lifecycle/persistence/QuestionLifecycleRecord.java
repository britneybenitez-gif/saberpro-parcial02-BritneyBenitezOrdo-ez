package co.edu.unicauca.saberpro.lifecycle.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.UUID;

@Entity
@Table(name = "question_lifecycle")
public class QuestionLifecycleRecord {
    public enum State { DRAFT, PENDING_REVIEW }

    @Id
    @Column(name = "question_id")
    private UUID questionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_state", nullable = false)
    private State state;

    @Version
    private Long version;

    protected QuestionLifecycleRecord() {}

    public static QuestionLifecycleRecord draft(UUID questionId) {
        var record = new QuestionLifecycleRecord();
        record.questionId = questionId;
        record.state = State.DRAFT;
        return record;
    }

    public void submitForReview() {
        if (state == State.DRAFT) {
            state = State.PENDING_REVIEW;
        }
    }

    public State getState() { return state; }
}
