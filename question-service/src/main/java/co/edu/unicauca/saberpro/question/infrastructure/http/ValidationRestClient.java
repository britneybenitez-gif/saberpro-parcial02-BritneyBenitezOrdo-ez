package co.edu.unicauca.saberpro.question.infrastructure.http;

import co.edu.unicauca.saberpro.contracts.QuestionInput;
import co.edu.unicauca.saberpro.contracts.ValidationResult;
import co.edu.unicauca.saberpro.question.application.port.QuestionValidation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ValidationRestClient implements QuestionValidation {
    private final RestTemplate http;
    private final String url;

    public ValidationRestClient(RestTemplate http, @Value("${saberpro.validation-url}") String baseUrl) {
        this.http = http;
        this.url = baseUrl + "/validate";
    }

    @Override
    public ValidationResult validate(QuestionInput input) {
        try {
            var response = http.postForEntity(url, input, ValidationResult.class);
            var result = response.getBody();
            if (response.getStatusCode().value() == 200 && result != null && result.valid()) {
                return result;
            }
        } catch (HttpClientErrorException.BadRequest ex) {
            // Conserva los errores del servicio que evaluó la pregunta.
            var result = ex.getResponseBodyAs(ValidationResult.class);
            if (result != null && !result.valid() && !result.errors().isEmpty()) {
                return result;
            }
        }
        throw new RestClientException("Respuesta inesperada de Validation Service.");
    }
}
