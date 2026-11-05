package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.repository.QuestionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public class QuestionRepositoryJpaImpl implements QuestionRepository {
    private final SpringQuestionDataRepository data;
    private final QuestionDomainMapper mapper;

    public QuestionRepositoryJpaImpl(SpringQuestionDataRepository data, QuestionDomainMapper mapper) {
        this.data = data;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Question save(Question question) {
        return mapper.toDomain(data.save(mapper.toRecord(question)));
    }

    @Override
    public Optional<Question> findById(UUID id) {
        return data.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) { return data.existsById(id); }
}
