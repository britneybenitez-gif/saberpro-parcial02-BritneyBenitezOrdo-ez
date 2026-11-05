package co.edu.unicauca.saberpro.question.infrastructure.web;

import co.edu.unicauca.saberpro.contracts.ValidationResult;
import co.edu.unicauca.saberpro.question.application.InvalidQuestion;
import co.edu.unicauca.saberpro.question.application.QuestionNotFound;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import java.util.List;

@RestControllerAdvice
public class ApiErrors {
    @ExceptionHandler(InvalidQuestion.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResult invalid(InvalidQuestion ex) { return ex.getResult(); }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationResult malformed() {
        return new ValidationResult(false, List.of("El cuerpo debe ser una pregunta JSON válida."));
    }

    @ExceptionHandler(QuestionNotFound.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFound(QuestionNotFound ex) { return new ApiError(ex.getMessage()); }

    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiError unavailableHttpService() {
        return new ApiError("No se pudo obtener respuesta del servicio requerido.");
    }

    @ExceptionHandler(AmqpException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiError unavailableBroker() {
        return new ApiError("No se pudo publicar el comando en RabbitMQ.");
    }

    public record ApiError(String message) {}
}
