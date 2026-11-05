package co.edu.unicauca.saberpro.question.domain.repository;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository {
    Question save(Question question);
    Optional<Question> findById(UUID id);
    boolean existsById(UUID id);
}
