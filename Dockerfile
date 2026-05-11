# Multi-stage build pour l'API Spring Boot dans pdfapi/
FROM maven:3.9.9-amazoncorretto-17 AS build

WORKDIR /app

# Copier le wrapper Maven et le projet Spring Boot
COPY pdfapi/pom.xml ./pdfapi/
COPY pdfapi/mvnw ./pdfapi/
COPY pdfapi/.mvn ./pdfapi/.mvn
COPY pdfapi/src ./pdfapi/src
COPY src/CalculatriceApp ./src/CalculatriceApp

WORKDIR /app/pdfapi
RUN chmod +x mvnw
RUN ./mvnw -B -Dmaven.test.skip=true clean package

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/pdfapi/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
