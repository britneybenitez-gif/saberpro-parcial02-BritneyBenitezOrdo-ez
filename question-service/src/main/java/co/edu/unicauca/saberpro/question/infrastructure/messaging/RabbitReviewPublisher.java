package co.edu.unicauca.saberpro.question.infrastructure.messaging;

import co.edu.unicauca.saberpro.contracts.ReviewMessagingConfiguration;
import co.edu.unicauca.saberpro.contracts.SubmitQuestionForReview;
import co.edu.unicauca.saberpro.question.application.port.ReviewPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitReviewPublisher implements ReviewPublisher {
    private final RabbitTemplate rabbit;

    public RabbitReviewPublisher(RabbitTemplate rabbit) { this.rabbit = rabbit; }

    @Override
    public void publish(SubmitQuestionForReview command) {
        rabbit.convertAndSend(ReviewMessagingConfiguration.EXCHANGE,
                ReviewMessagingConfiguration.ROUTING_KEY, command);
    }
}
