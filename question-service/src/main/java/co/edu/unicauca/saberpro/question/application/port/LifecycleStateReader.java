package co.edu.unicauca.saberpro.question.application.port;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import java.util.Optional;
import java.util.UUID;

public interface LifecycleStateReader {
    Optional<Question.State> findTransition(UUID questionId);
}
