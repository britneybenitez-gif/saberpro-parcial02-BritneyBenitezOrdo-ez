package co.edu.unicauca.saberpro.question.application.port;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import co.edu.unicauca.saberpro.contracts.ValidationResult;

public interface QuestionValidation {
    ValidationResult validate(QuestionInput input);
}
