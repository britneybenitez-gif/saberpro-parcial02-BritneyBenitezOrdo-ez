package co.edu.unicauca.saberpro.contracts;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ReviewMessagingConfiguration {
    public static final String EXCHANGE = "saberpro.direct.exchange";
    public static final String ROUTING_KEY = "question.submit";
    public static final String QUEUE = "question.submit.review.queue";

    // Ambos servicios declaran la misma topología durable, incluso si arrancan por separado.
    @Bean
    Declarables reviewTopology() {
        var exchange = new DirectExchange(EXCHANGE);
        var queue = new Queue(QUEUE, true);
        return new Declarables(exchange, queue, BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY));
    }

    @Bean
    Jackson2JsonMessageConverter reviewMessageConverter() {
        return new Jackson2JsonMessageConverter("co.edu.unicauca.saberpro.contracts");
    }
}
