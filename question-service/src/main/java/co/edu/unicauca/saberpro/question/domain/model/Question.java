package co.edu.unicauca.saberpro.question.domain.model;

import java.util.List;
import java.util.UUID;

public final class Question {
    public enum State { DRAFT, PENDING_REVIEW }

    private final UUID id;
    private final String text;
    private final List<AnswerOption> options;
    private final String correctOptionId;
    private final String justification;
    private final State state;

    public Question(UUID id, String text, List<AnswerOption> options, String correctOptionId,
                    String justification, State state) {
        this.id = id;
        this.text = text;
        this.options = List.copyOf(options);
        this.correctOptionId = correctOptionId;
        this.justification = justification;
        this.state = state;
    }

    public static Question draft(String text, List<AnswerOption> options, String correctOptionId,
                                 String justification) {
        return new Question(UUID.randomUUID(), text, options, correctOptionId, justification, State.DRAFT);
    }

    public Question withState(State currentState) {
        return new Question(id, text, options, correctOptionId, justification, currentState);
    }

    public UUID getId() { return id; }
    public String getText() { return text; }
    public List<AnswerOption> getOptions() { return options; }
    public String getCorrectOptionId() { return correctOptionId; }
    public String getJustification() { return justification; }
    public State getState() { return state; }
}
