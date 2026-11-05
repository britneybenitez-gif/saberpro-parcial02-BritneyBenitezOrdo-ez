package co.edu.unicauca.saberpro.validation;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class QuestionValidatorTest {
    private static final List<QuestionInput.Option> OPTIONS = List.of(
            new QuestionInput.Option("A", "Una respuesta de más de veinte caracteres"),
            new QuestionInput.Option("B", "Otra respuesta"),
            new QuestionInput.Option("C", "Tercera"),
            new QuestionInput.Option("D", "Cuarta"));
    private final QuestionValidator validator = new QuestionValidator(20);

    @Test
    void acceptsTheConfiguredMinimumAndRejectsOneCharacterLess() {
        String reason = "12345678901234567890";
        assertThat(validator.validate(new QuestionInput("Pregunta", OPTIONS, "A", reason)).valid()).isTrue();
        assertThat(validator.validate(new QuestionInput("Pregunta", OPTIONS, "A", reason.substring(1))).valid())
                .isFalse();
    }

    @Test
    void rejectsAnExplanationThatOnlyCopiesTheCorrectOption() {
        var input = new QuestionInput("Pregunta", OPTIONS, "A", "  UNA respuesta DE MÁS DE VEINTE caracteres  ");
        assertThat(validator.validate(input).errors()).singleElement().asString().contains("copiar");
    }

    @Test
    void reportsCountKeyAndJustificationTogether() {
        var input = new QuestionInput("Pregunta", OPTIONS.subList(0, 3), "Z", "  ");
        assertThat(validator.validate(input).errors()).hasSize(3);
    }

    @Test
    void returnsErrorsForNullAndDuplicateOptions() {
        var options = Arrays.asList(OPTIONS.get(0), OPTIONS.get(0), null, OPTIONS.get(3));
        var input = new QuestionInput("Pregunta", options, "A", "Esta explicación aporta una razón distinta.");
        assertThat(validator.validate(input).errors()).hasSize(3);
    }
}
