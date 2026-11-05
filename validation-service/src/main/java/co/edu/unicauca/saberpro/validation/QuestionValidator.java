package co.edu.unicauca.saberpro.validation;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import co.edu.unicauca.saberpro.contracts.ValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Service
public class QuestionValidator {
    private final int minimumLength;

    public QuestionValidator(@Value("${saberpro.justification-min-length}") int minimumLength) {
        if (minimumLength < 1) {
            throw new IllegalArgumentException("El mínimo de la justificación debe ser positivo.");
        }
        this.minimumLength = minimumLength;
    }

    public ValidationResult validate(QuestionInput input) {
        if (input == null) {
            return new ValidationResult(false, List.of("La pregunta es obligatoria."));
        }
        var errors = new ArrayList<String>();
        if (clean(input.text()).isEmpty()) {
            errors.add("El enunciado es obligatorio.");
        }

        var options = input.options() == null ? List.<QuestionInput.Option>of() : input.options();
        if (options.size() != 4) {
            errors.add("Debe haber exactamente 4 opciones.");
        }

        var ids = new HashSet<String>();
        var texts = new HashSet<String>();
        String correctId = clean(input.correctOptionId());
        String correctText = "";
        for (var option : options) {
            if (option == null) {
                errors.add("Cada opción debe tener un id y un texto.");
                continue;
            }
            String id = clean(option.id());
            String text = normalized(option.text());
            if (id.isEmpty()) {
                errors.add("Cada opción debe tener un id.");
            } else if (!ids.add(id)) {
                errors.add("Id de opción repetido: " + id);
            }
            if (text.isEmpty()) {
                errors.add("Cada opción debe tener texto.");
            } else if (!texts.add(text)) {
                errors.add("Las opciones deben tener textos distintos.");
            }
            if (id.equals(correctId)) {
                correctText = text;
            }
        }
        if (correctId.isEmpty() || !ids.contains(correctId)) {
            errors.add("La clave correcta debe coincidir con el id de una opción.");
        }

        String justification = clean(input.justification());
        if (justification.codePointCount(0, justification.length()) < minimumLength) {
            errors.add("La justificación debe tener al menos " + minimumLength + " caracteres.");
        }
        if (!correctText.isEmpty() && correctText.equals(normalized(justification))) {
            errors.add("La justificación debe explicar la respuesta, sin copiar el texto de la opción correcta.");
        }
        return new ValidationResult(errors.isEmpty(), errors);
    }

    private static String clean(String value) {
        return value == null ? "" : value.strip();
    }

    private static String normalized(String value) {
        return clean(value).replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
