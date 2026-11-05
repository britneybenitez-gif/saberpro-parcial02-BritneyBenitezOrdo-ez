package co.edu.unicauca.saberpro.lifecycle.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface QuestionLifecycleRepository extends JpaRepository<QuestionLifecycleRecord, UUID> {}
