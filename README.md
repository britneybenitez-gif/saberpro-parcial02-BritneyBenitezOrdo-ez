# Banco de Preguntas Saber PRO · Parcial 02

Tres microservicios permiten crear una pregunta, validar sus cuatro opciones y enviarla a revisión. El proyecto se abre desde el `pom.xml` de esta carpeta. Requiere Java 17 o posterior al ejecutarlo desde el IDE.

## 1. Ejecutar con Docker Compose

Desde la carpeta que contiene `compose.yaml`, inicia el sistema completo.

```bash
docker compose up --build -d
docker compose logs -f validation-service question-service lifecycle-service
```

Espera a que los tres servicios muestren `Started`. Pulsa `Ctrl+C` para salir de la vista de logs. Las bases H2 y la cola usan volúmenes persistentes. Para detener el sistema usa `docker compose down`.

| Componente | Dirección local | Qué comprobar |
| --- | --- | --- |
| Validation Service | `http://localhost:8082` | `POST /validate` evalúa la estructura y la justificación. |
| Question Service | `http://localhost:8083` | `POST /questions` crea el borrador. `POST /questions/{id}/submit` solicita la revisión. |
| Lifecycle Service | `http://localhost:8084` | `GET /operations/{operationId}` devuelve el resultado del comando procesado. |
| RabbitMQ | `http://localhost:15672` | Panel de gestión. Usuario local `saberpro` y contraseña `saberpro_local`. El puerto AMQP es `5672`. |

### Desde VS Code o IntelliJ

Si prefieres ejecutar Java desde el IDE, inicia únicamente RabbitMQ en Docker y compila desde la raíz con un JDK 17+ y Maven 3.9+.

```bash
docker compose up -d rabbitmq
mvn clean verify
```

Abre la carpeta raíz e importa el proyecto Maven. Ejecuta los métodos `main` de `ValidationServiceApplication`, `LifecycleServiceApplication` y `QuestionServiceApplication`. También puedes abrir tres terminales en la raíz y ejecutar un JAR en cada una.

```bash
java -jar validation-service/target/validation-service-1.0.0.jar
java -jar lifecycle-service/target/lifecycle-service-1.0.0.jar
java -jar question-service/target/question-service-1.0.0.jar
```

## 2. Ejecutar la colección de Postman

1. En Postman Desktop selecciona **Import** y abre [SaberPRO_Parcial02.postman_collection.json](postman/SaberPRO_Parcial02.postman_collection.json).
2. Abre **Saber PRO · Parcial 02** y selecciona **Run collection**. Ejecuta las diez solicitudes en el orden guardado. La colección ya incluye las URL y las credenciales locales.
3. Revisa los resultados de las pruebas. El Runner guarda `questionId` tras el `201` y `operationId` tras el `202`. Reutiliza ambos para comprobar el estado final. Las consultas de seguimiento se repiten mientras Lifecycle procesa el comando o RabbitMQ actualiza sus estadísticas.

| Caso | Resultado esperado |
| --- | --- |
| Crear una pregunta con cuatro opciones distintas, clave C existente y justificación válida | Validation devuelve `200`. Question devuelve `201 Created`, el ID, `state: DRAFT` y la cabecera `Location`. |
| Conservar opciones y clave, e introducir «Porque sí.» como justificación | Question devuelve `400 Bad Request` con el error pedagógico. |
| Enviar tres opciones y la clave Z | Question devuelve `400` con la misma lista de errores, en el mismo orden que Validation. El repositorio solo se invoca tras una validación exitosa. |
| Enviar el borrador a revisión | Question devuelve `202 Accepted` con un UUID en `operationId` y `status: PROCESSING`. |
| Consultar la operación y luego la pregunta | Lifecycle informa `COMPLETED` y `PENDING_REVIEW`. Al leer la pregunta se recuperan las cuatro opciones originales y su nuevo estado. |
| Leer los contadores de la cola antes y después del envío | Aumentan las entregas (`deliver_get`) y las confirmaciones del consumidor (`ack`). |

Quien tenga Newman instalado puede ejecutar la misma colección desde la raíz.

```bash
newman run postman/SaberPRO_Parcial02.postman_collection.json
```

## 3. Revisar la solución

**Validación síncrona.** El cuerpo conserva los campos del Taller 09, `text`, `options` con `id` y `text`, y `correctOptionId`. Se añade `justification`. `ValidationRestClient` llama a `POST /validate` con `RestTemplate`. `QuestionService.create` solo construye y guarda el borrador cuando recibe una validación exitosa.

**Criterio pedagógico adoptado.** El enunciado pide una justificación válida y deja abierta su medida. Esta implementación exige **20 caracteres** como mínimo después de quitar espacios en los extremos. También rechaza una copia literal del texto de la opción correcta, aun cuando cambien las mayúsculas o el espaciado. El mínimo se ajusta con la variable `JUSTIFICATION_MIN_LENGTH` en Validation. Los casos incluidos utilizan el valor predeterminado.

**Persistencia limpia.** `Question` es una clase Java pura. `QuestionRepositoryJpaImpl` implementa el puerto `QuestionRepository`, inyecta `SpringQuestionDataRepository` y utiliza `QuestionDomainMapper` para las dos conversiones. H2 guarda cada pregunta con sus cuatro filas de opciones en una transacción. La clave compuesta permite reutilizar A–D en preguntas distintas. El campo de posición conserva el orden original.

| Pieza | Archivo |
| --- | --- |
| Modelo de dominio | [Question.java](question-service/src/main/java/co/edu/unicauca/saberpro/question/domain/model/Question.java) |
| Cabecera JPA y relación con las opciones | [QuestionRecord.java](question-service/src/main/java/co/edu/unicauca/saberpro/question/infrastructure/jpa/record/QuestionRecord.java) |
| Conversión en ambos sentidos | [QuestionDomainMapper.java](question-service/src/main/java/co/edu/unicauca/saberpro/question/infrastructure/jpa/QuestionDomainMapper.java) |
| Adaptador del repositorio | [QuestionRepositoryJpaImpl.java](question-service/src/main/java/co/edu/unicauca/saberpro/question/infrastructure/jpa/QuestionRepositoryJpaImpl.java) |
| Reglas pedagógicas y estructurales | [QuestionValidator.java](validation-service/src/main/java/co/edu/unicauca/saberpro/validation/QuestionValidator.java) |
| Invocación HTTP a Validation | [ValidationRestClient.java](question-service/src/main/java/co/edu/unicauca/saberpro/question/infrastructure/http/ValidationRestClient.java) |
| Publicación y consumo del comando | [RabbitReviewPublisher.java](question-service/src/main/java/co/edu/unicauca/saberpro/question/infrastructure/messaging/RabbitReviewPublisher.java) y [ReviewCommandListener.java](lifecycle-service/src/main/java/co/edu/unicauca/saberpro/lifecycle/messaging/ReviewCommandListener.java) |

**Envío a revisión.** `RabbitTemplate` publica `SubmitQuestionForReview` en la exchange directa `saberpro.direct.exchange` con la routing key `question.submit`. La cola durable `question.submit.review.queue` recibe el comando. El controlador devuelve `202` al publicar. El `@RabbitListener` de Lifecycle aplica la transición `DRAFT → PENDING_REVIEW` y guarda, en una misma transacción, el estado de la pregunta y el resultado de la operación. La reentrega del mismo `operationId` conserva un solo resultado.

**Consultar el estado.** Question guarda el estado inicial en `questions.initial_state`. Lifecycle mantiene el estado vigente en `question_lifecycle.current_state` y los resultados en `review_operations`, en su propia base H2. `GET /questions/{id}` combina el contenido persistido con la última transición. Antes de la primera transición devuelve `DRAFT`. Si se consulta `GET /operations/{operationId}` antes de que el consumidor confirme la transacción, Lifecycle responde temporalmente `404` y el Runner vuelve a consultar.

## 4. Comprobar RabbitMQ y consultar las evidencias

Abre [la consola local](http://localhost:15672), entra en **Queues and Streams** y selecciona **question.submit.review.queue**. Un mensaje puede pasar por la cola entre dos refrescos del panel. Los contadores de entrega y acuse permiten observar el consumo.

Para ver un mensaje en espera con el despliegue completo, ejecuta `docker compose stop lifecycle-service`. En Postman envía las solicitudes **02** y **07** individualmente. La segunda devuelve `202` y el panel muestra un mensaje en **Ready**. Arranca el consumidor con `docker compose start lifecycle-service` y ejecuta la solicitud **08**. El mismo `operationId` debe figurar como `COMPLETED`.

| Evidencia incluida | Qué muestra |
| --- | --- |
| [Compilación y pruebas Maven](evidencias/maven-verificacion.txt) | Los tres microservicios compilan con Java 17 y las nueve pruebas JUnit pasan. |
| [Ejecución de Postman con Newman](evidencias/postman-newman.txt) | Códigos HTTP, aserciones y resumen de la colección. El [reporte JUnit de Newman](evidencias/postman-junit.xml) contiene el resultado verificable por herramientas. |
| [Cola con Lifecycle detenido](evidencias/rabbitmq-01-en-cola.png) | Captura del panel con **Ready = 1** y **Consumers = 0** después del `202`. |
| [Cola después del consumo](evidencias/rabbitmq-02-entregada.png) | Captura del mismo panel con **Ready = 0** y **Consumers = 1**. |
| [Resultados de la ejecución](evidencias/resultados.json) | Cuerpos HTTP, UUID reales, incremento de los acuses, recuperación tras reiniciar Question y conteo de filas H2. |

Las pruebas JUnit se ejecutan con `mvn clean verify`. Cubren los límites de validación, el rechazo previo al guardado, el envío de una pregunta inexistente, el mapeo de ida y vuelta en H2 y la reentrega de comandos de revisión.

El contrato de entrada toma como referencia la [rama del Taller 09 del equipo](https://github.com/martin1061/LaboratorioIngenieriaSoftware2/tree/taller09-microservicios).
