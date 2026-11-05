package co.edu.unicauca.saberpro.lifecycle;

import co.edu.unicauca.saberpro.contracts.ReviewMessagingConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ReviewMessagingConfiguration.class)
public class LifecycleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LifecycleServiceApplication.class, args);
    }
}
