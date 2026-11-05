package co.edu.unicauca.saberpro.contracts;

import java.util.List;

public record QuestionInput(String text, List<Option> options, String correctOptionId, String justification) {
    public record Option(String id, String text) {}
}
