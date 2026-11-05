package co.edu.unicauca.saberpro.question;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import co.edu.unicauca.saberpro.contracts.ValidationResult;
import co.edu.unicauca.saberpro.question.application.InvalidQuestion;
import co.edu.unicauca.saberpro.question.application.QuestionNotFound;
import co.edu.unicauca.saberpro.question.application.QuestionService;
import co.edu.unicauca.saberpro.question.application.port.LifecycleStateReader;
import co.edu.unicauca.saberpro.question.application.port.QuestionValidation;
import co.edu.unicauca.saberpro.question.application.port.ReviewPublisher;
import co.edu.unicauca.saberpro.question.domain.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class QuestionServiceTest {
    private final QuestionRepository questions = mock(QuestionRepository.class);
    private final QuestionValidation validation = mock(QuestionValidation.class);
    private final ReviewPublisher publisher = mock(ReviewPublisher.class);
    private final LifecycleStateReader lifecycle = mock(LifecycleStateReader.class);
    private final QuestionService service = new QuestionService(questions, validation, publisher, lifecycle);

    @Test
    void rejectionPreservesAllErrorsAndNeverReachesPersistence() {
        var input = new QuestionInput("Pregunta", List.of(), "Z", "Corta");
        var expected = new ValidationResult(false, List.of("Faltan opciones", "Clave inexistente"));
        when(validation.validate(input)).thenReturn(expected);
        var rejected = assertThrows(InvalidQuestion.class, () -> service.create(input));
        assertThat(rejected.getResult()).isEqualTo(expected);
        verifyNoInteractions(questions, publisher, lifecycle);
    }

    @Test
    void onlyExistingQuestionsCanPublishAReviewCommand() {
        UUID id = UUID.randomUUID();
        assertThatThrownBy(() -> service.submit(id)).isInstanceOf(QuestionNotFound.class);
        verifyNoInteractions(publisher, lifecycle);
    }
}
