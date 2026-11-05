FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /project
COPY pom.xml .
COPY shared-contracts shared-contracts
COPY validation-service validation-service
COPY question-service question-service
COPY lifecycle-service lifecycle-service
RUN mvn -B -ntp -DskipTests package

FROM eclipse-temurin:17-jre
ARG SERVICE
WORKDIR /app
COPY --from=build /project/${SERVICE}/target/${SERVICE}-1.0.0.jar /app/service.jar
ENTRYPOINT ["java", "-jar", "/app/service.jar"]
