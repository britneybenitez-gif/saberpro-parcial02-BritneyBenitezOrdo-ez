package co.edu.unicauca.saberpro.question.application;

import java.util.UUID;

public class QuestionNotFound extends RuntimeException {
    public QuestionNotFound(UUID id) {
        super("La pregunta " + id + " no existe.");
    }
}
