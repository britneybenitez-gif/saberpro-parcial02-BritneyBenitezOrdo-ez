package co.edu.unicauca.saberpro.question.infrastructure.config;

import co.edu.unicauca.saberpro.contracts.ReviewMessagingConfiguration;
import co.edu.unicauca.saberpro.question.application.QuestionService;
import co.edu.unicauca.saberpro.question.application.port.LifecycleStateReader;
import co.edu.unicauca.saberpro.question.application.port.QuestionValidation;
import co.edu.unicauca.saberpro.question.application.port.ReviewPublisher;
import co.edu.unicauca.saberpro.question.domain.repository.QuestionRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;

@Configuration(proxyBeanMethods = false)
@Import(ReviewMessagingConfiguration.class)
public class QuestionConfiguration {
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.connectTimeout(Duration.ofSeconds(2)).readTimeout(Duration.ofSeconds(3)).build();
    }

    @Bean
    QuestionService questionService(QuestionRepository questions, QuestionValidation validation,
                                    ReviewPublisher publisher, LifecycleStateReader lifecycle) {
        return new QuestionService(questions, validation, publisher, lifecycle);
    }
}
