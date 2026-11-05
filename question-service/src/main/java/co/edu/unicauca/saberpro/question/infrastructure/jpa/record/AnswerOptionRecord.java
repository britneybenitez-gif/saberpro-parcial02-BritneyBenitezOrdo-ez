package co.edu.unicauca.saberpro.question.infrastructure.jpa.record;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "answer_options")
public class AnswerOptionRecord {
    @EmbeddedId
    private AnswerOptionId id;

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionRecord question;

    @Lob
    @Column(name = "option_text", nullable = false)
    private String text;

    @Column(name = "option_position", nullable = false)
    private int position;

    protected AnswerOptionRecord() {}

    AnswerOptionRecord(QuestionRecord question, String optionId, String text, int position) {
        this.id = new AnswerOptionId(question.getId(), optionId);
        this.question = question;
        this.text = text;
        this.position = position;
    }

    public String getOptionId() { return id.getOptionId(); }
    public String getText() { return text; }
}
