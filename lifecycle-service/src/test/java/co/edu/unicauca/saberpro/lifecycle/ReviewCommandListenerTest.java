package co.edu.unicauca.saberpro.lifecycle;

import co.edu.unicauca.saberpro.contracts.SubmitQuestionForReview;
import co.edu.unicauca.saberpro.lifecycle.messaging.ReviewCommandListener;
import co.edu.unicauca.saberpro.lifecycle.persistence.QuestionLifecycleRecord;
import co.edu.unicauca.saberpro.lifecycle.persistence.QuestionLifecycleRepository;
import co.edu.unicauca.saberpro.lifecycle.persistence.ReviewOperationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@Import(ReviewCommandListener.class)
class ReviewCommandListenerTest {
    @Autowired ReviewCommandListener listener;
    @Autowired ReviewOperationRepository operations;
    @Autowired QuestionLifecycleRepository states;
    @Autowired TestEntityManager em;

    @Test
    void redeliveryKeepsOneDurableTransitionForTheSameOperation() {
        var command = new SubmitQuestionForReview(UUID.randomUUID(), UUID.randomUUID());
        listener.receive(command);
        em.flush();
        em.clear();
        listener.receive(command);
        em.flush();
        em.clear();
        assertThat(operations.count()).isEqualTo(1);
        var receipt = operations.findById(command.operationId()).orElseThrow();
        assertThat(receipt.getQuestionId()).isEqualTo(command.questionId());
        assertThat(receipt.getState()).isEqualTo("PENDING_REVIEW");
        assertThat(states.findById(command.questionId()).orElseThrow().getState())
                .isEqualTo(QuestionLifecycleRecord.State.PENDING_REVIEW);
    }

    @Test
    void separateSubmissionsOfOneQuestionKeepOneStateAndBothOperationReceipts() {
        UUID questionId = UUID.randomUUID();
        listener.receive(new SubmitQuestionForReview(UUID.randomUUID(), questionId));
        em.flush();
        em.clear();
        listener.receive(new SubmitQuestionForReview(UUID.randomUUID(), questionId));
        em.flush();
        em.clear();
        assertThat(operations.count()).isEqualTo(2);
        assertThat(states.count()).isEqualTo(1);
        assertThat(states.findById(questionId).orElseThrow().getState())
                .isEqualTo(QuestionLifecycleRecord.State.PENDING_REVIEW);
    }
}
