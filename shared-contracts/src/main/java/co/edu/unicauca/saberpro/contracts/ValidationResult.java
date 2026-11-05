package co.edu.unicauca.saberpro.contracts;

import java.util.List;

public record ValidationResult(boolean valid, List<String> errors) {
    public ValidationResult {
        errors = List.copyOf(errors);
    }
}
