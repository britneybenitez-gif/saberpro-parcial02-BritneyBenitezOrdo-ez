package co.edu.unicauca.saberpro.question.application;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import co.edu.unicauca.saberpro.contracts.SubmitQuestionForReview;
import co.edu.unicauca.saberpro.question.application.port.LifecycleStateReader;
import co.edu.unicauca.saberpro.question.application.port.QuestionValidation;
import co.edu.unicauca.saberpro.question.application.port.ReviewPublisher;
import co.edu.unicauca.saberpro.question.domain.model.AnswerOption;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import co.edu.unicauca.saberpro.question.domain.repository.QuestionRepository;
import java.util.UUID;

public class QuestionService {
    private final QuestionRepository questions;
    private final QuestionValidation validation;
    private final ReviewPublisher publisher;
    private final LifecycleStateReader lifecycle;

    public QuestionService(QuestionRepository questions, QuestionValidation validation,
                           ReviewPublisher publisher, LifecycleStateReader lifecycle) {
        this.questions = questions;
        this.validation = validation;
        this.publisher = publisher;
        this.lifecycle = lifecycle;
    }

    public Question create(QuestionInput input) {
        var result = validation.validate(input);
        if (!result.valid()) {
            throw new InvalidQuestion(result);
        }
        var options = input.options().stream()
                .map(option -> new AnswerOption(option.id().strip(), option.text().strip()))
                .toList();
        var question = Question.draft(input.text().strip(), options, input.correctOptionId().strip(),
                input.justification().strip());
        return questions.save(question);
    }

    public Question find(UUID id) {
        Question question = questions.findById(id).orElseThrow(() -> new QuestionNotFound(id));
        return lifecycle.findTransition(id).map(question::withState).orElse(question);
    }

    public UUID submit(UUID id) {
        if (!questions.existsById(id)) {
            throw new QuestionNotFound(id);
        }
        var command = new SubmitQuestionForReview(UUID.randomUUID(), id);
        publisher.publish(command);
        return command.operationId();
    }
}
