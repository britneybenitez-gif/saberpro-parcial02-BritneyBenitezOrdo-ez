package co.edu.unicauca.saberpro.question.infrastructure.jpa.record;

import co.edu.unicauca.saberpro.question.domain.model.Question;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
public class QuestionRecord {
    @Id
    private UUID id;

    @Lob
    @Column(name = "question_text", nullable = false)
    private String text;

    @Column(name = "correct_option_id", nullable = false, columnDefinition = "varchar")
    private String correctOptionId;

    @Lob
    @Column(nullable = false)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(name = "initial_state", nullable = false)
    private Question.State state;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<AnswerOptionRecord> options = new ArrayList<>();

    protected QuestionRecord() {}

    public QuestionRecord(UUID id, String text, String correctOptionId, String justification,
                          Question.State state) {
        this.id = id;
        this.text = text;
        this.correctOptionId = correctOptionId;
        this.justification = justification;
        this.state = state;
    }

    public void addOption(String optionId, String optionText) {
        options.add(new AnswerOptionRecord(this, optionId, optionText, options.size()));
    }

    public UUID getId() { return id; }
    public String getText() { return text; }
    public String getCorrectOptionId() { return correctOptionId; }
    public String getJustification() { return justification; }
    public Question.State getState() { return state; }
    public List<AnswerOptionRecord> getOptions() { return options; }
}
