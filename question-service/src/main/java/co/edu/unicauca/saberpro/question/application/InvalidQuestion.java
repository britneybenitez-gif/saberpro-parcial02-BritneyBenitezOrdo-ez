package co.edu.unicauca.saberpro.question.application;

import co.edu.unicauca.saberpro.contracts.ValidationResult;

public class InvalidQuestion extends RuntimeException {
    private final ValidationResult result;

    public InvalidQuestion(ValidationResult result) {
        super("La pregunta incumple las reglas de validación.");
        this.result = result;
    }

    public ValidationResult getResult() { return result; }
}
