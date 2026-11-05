package co.edu.unicauca.saberpro.question.infrastructure.http;

import co.edu.unicauca.saberpro.contracts.QuestionStateResponse;
import co.edu.unicauca.saberpro.question.application.port.LifecycleStateReader;
import co.edu.unicauca.saberpro.question.domain.model.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;
import java.util.UUID;

@Component
public class LifecycleRestClient implements LifecycleStateReader {
    private final RestTemplate http;
    private final String url;

    public LifecycleRestClient(RestTemplate http, @Value("${saberpro.lifecycle-url}") String baseUrl) {
        this.http = http;
        this.url = baseUrl + "/lifecycle/questions/{id}/state";
    }

    @Override
    public Optional<Question.State> findTransition(UUID questionId) {
        try {
            var response = http.getForObject(url, QuestionStateResponse.class, questionId);
            if (response == null || !questionId.equals(response.questionId())
                    || !Question.State.PENDING_REVIEW.name().equals(response.state())) {
                throw new RestClientException("Respuesta inesperada de Lifecycle Service.");
            }
            return Optional.of(Question.State.PENDING_REVIEW);
        } catch (HttpClientErrorException.NotFound ex) {
            // El contenido ya existe en H2, pero Lifecycle aún no registra una transición.
            return Optional.empty();
        }
    }
}
