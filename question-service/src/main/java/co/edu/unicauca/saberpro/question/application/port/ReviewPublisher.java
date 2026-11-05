package co.edu.unicauca.saberpro.question.application.port;

import co.edu.unicauca.saberpro.contracts.SubmitQuestionForReview;

public interface ReviewPublisher {
    void publish(SubmitQuestionForReview command);
}
