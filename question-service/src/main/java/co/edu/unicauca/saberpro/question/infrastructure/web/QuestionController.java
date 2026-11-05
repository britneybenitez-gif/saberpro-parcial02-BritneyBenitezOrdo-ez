package co.edu.unicauca.saberpro.question.infrastructure.web;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import co.edu.unicauca.saberpro.question.application.QuestionService;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/questions")
public class QuestionController {
    private final QuestionService service;

    public QuestionController(QuestionService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Question> create(@RequestBody(required = false) QuestionInput input) {
        Question created = service.create(input);
        return ResponseEntity.created(URI.create("/questions/" + created.getId())).body(created);
    }

    @GetMapping("/{id}")
    public Question find(@PathVariable UUID id) { return service.find(id); }

    @PostMapping("/{id}/submit")
    public ResponseEntity<Accepted> submit(@PathVariable UUID id) {
        return ResponseEntity.accepted().body(new Accepted(service.submit(id), "PROCESSING"));
    }

    public record Accepted(UUID operationId, String status) {}
}
