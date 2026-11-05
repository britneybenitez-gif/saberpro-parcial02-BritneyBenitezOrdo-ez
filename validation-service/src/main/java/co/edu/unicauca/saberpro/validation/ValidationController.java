package co.edu.unicauca.saberpro.validation;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import co.edu.unicauca.saberpro.contracts.ValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {
    private final QuestionValidator validator;

    public ValidationController(QuestionValidator validator) {
        this.validator = validator;
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validate(@RequestBody(required = false) QuestionInput input) {
        ValidationResult result = validator.validate(input);
        return result.valid() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);
    }
}
