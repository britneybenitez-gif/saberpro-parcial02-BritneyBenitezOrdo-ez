package co.edu.unicauca.saberpro.question.infrastructure.jpa;

import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.infrastructure.jpa.record.QuestionRecord;
import org.springframework.stereotype.Component;

@Component
public class QuestionDomainMapper {
    public QuestionRecord toRecord(Question question) {
        var record = new QuestionRecord(question.getId(), question.getText(), question.getCorrectOptionId(),
                question.getJustification(), question.getState());
        question.getOptions().forEach(option -> record.addOption(option.id(), option.text()));
        return record;
    }

    public Question toDomain(QuestionRecord record) {
        var options = record.getOptions().stream()
                .map(option -> new AnswerOption(option.getOptionId(), option.getText()))
                .toList();
        return new Question(record.getId(), record.getText(), options, record.getCorrectOptionId(),
                record.getJustification(), record.getState());
    }
}
