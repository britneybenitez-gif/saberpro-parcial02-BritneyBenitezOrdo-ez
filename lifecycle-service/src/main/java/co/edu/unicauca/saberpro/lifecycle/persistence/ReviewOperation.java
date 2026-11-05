package co.edu.unicauca.saberpro.lifecycle.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "review_operations")
public class ReviewOperation {
    @Id
    @Column(name = "operation_id")
    private UUID operationId;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "resulting_state", nullable = false)
    private String state;

    protected ReviewOperation() {}

    public static ReviewOperation completed(UUID operationId, UUID questionId, QuestionLifecycleRecord.State state) {
        var operation = new ReviewOperation();
        operation.operationId = operationId;
        operation.questionId = questionId;
        operation.state = state.name();
        return operation;
    }

    public UUID getOperationId() { return operationId; }
    public UUID getQuestionId() { return questionId; }
    public String getState() { return state; }
}
