package co.edu.unicauca.saberpro.lifecycle.messaging;

import co.edu.unicauca.saberpro.contracts.ReviewMessagingConfiguration;
import co.edu.unicauca.saberpro.contracts.SubmitQuestionForReview;
import co.edu.unicauca.saberpro.lifecycle.persistence.QuestionLifecycleRecord;
import co.edu.unicauca.saberpro.lifecycle.persistence.QuestionLifecycleRepository;
import co.edu.unicauca.saberpro.lifecycle.persistence.ReviewOperation;
import co.edu.unicauca.saberpro.lifecycle.persistence.ReviewOperationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReviewCommandListener {
    private static final Logger log = LoggerFactory.getLogger(ReviewCommandListener.class);
    private final ReviewOperationRepository operations;
    private final QuestionLifecycleRepository states;

    public ReviewCommandListener(ReviewOperationRepository operations, QuestionLifecycleRepository states) {
        this.operations = operations;
        this.states = states;
    }

    @RabbitListener(queues = ReviewMessagingConfiguration.QUEUE)
    @Transactional
    public void receive(SubmitQuestionForReview command) {
        if (operations.existsById(command.operationId())) {
            return; // una reentrega del mismo comando conserva el resultado anterior
        }
        var question = states.findById(command.questionId())
                .orElseGet(() -> QuestionLifecycleRecord.draft(command.questionId()));
        question.submitForReview();
        states.save(question);
        operations.save(ReviewOperation.completed(command.operationId(), command.questionId(), question.getState()));
        log.info("Pregunta {} en PENDING_REVIEW, operación {}", command.questionId(), command.operationId());
    }
}
