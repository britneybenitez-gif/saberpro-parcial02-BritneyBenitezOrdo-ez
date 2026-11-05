package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.infrastructure.jpa.record.QuestionRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringQuestionDataRepository extends JpaRepository<QuestionRecord, UUID> {
    @Override
    @EntityGraph(attributePaths = "options")
    Optional<QuestionRecord> findById(UUID id);
}
