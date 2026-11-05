package co.edu.unicauca.saberpro.lifecycle.web;

import co.edu.unicauca.saberpro.contracts.QuestionStateResponse;
import co.edu.unicauca.saberpro.lifecycle.persistence.QuestionLifecycleRepository;
import co.edu.unicauca.saberpro.lifecycle.persistence.ReviewOperationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
public class LifecycleController {
    private final ReviewOperationRepository operations;
    private final QuestionLifecycleRepository states;

    public LifecycleController(ReviewOperationRepository operations, QuestionLifecycleRepository states) {
        this.operations = operations;
        this.states = states;
    }

    @GetMapping("/operations/{operationId}")
    public ResponseEntity<OperationResult> operation(@PathVariable UUID operationId) {
        return ResponseEntity.of(operations.findById(operationId).map(op ->
                new OperationResult(op.getOperationId(), op.getQuestionId(), "COMPLETED", op.getState())));
    }

    @GetMapping("/lifecycle/questions/{id}/state")
    public ResponseEntity<QuestionStateResponse> state(@PathVariable UUID id) {
        return ResponseEntity.of(states.findById(id).map(question ->
                new QuestionStateResponse(id, question.getState().name())));
    }

    public record OperationResult(UUID operationId, UUID questionId, String status, String state) {}
}
