package co.edu.unicauca.saberpro.question;

import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.QuestionDomainMapper;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.QuestionRepositoryJpaImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@Import({QuestionDomainMapper.class, QuestionRepositoryJpaImpl.class})
class QuestionPersistenceTest {
    @Autowired QuestionRepositoryJpaImpl repository;
    @Autowired TestEntityManager em;

    @Test
    void storesAndRebuildsTheAggregateIncludingOptionOrderAndIndependentOptionKeys() {
        List<AnswerOption> options = List.of(new AnswerOption("C", "Tres"), new AnswerOption("A", "Uno"),
                new AnswerOption("D", "Cuatro"), new AnswerOption("B", "Dos"));
        var question = Question.draft("¿Cuál es par?", options, "B", "Dos es divisible entre dos.");
        repository.save(question);
        repository.save(Question.draft("Otra pregunta", options, "C", "Tres es el siguiente de dos."));
        em.flush();
        em.clear();

        var loaded = repository.findById(question.getId()).orElseThrow();
        assertThat(loaded).usingRecursiveComparison().isEqualTo(question);
        assertThat(((Number) em.getEntityManager().createNativeQuery("select count(*) from answer_options")
                .getSingleResult()).longValue()).isEqualTo(8);
    }
}
