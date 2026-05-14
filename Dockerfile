# Multi-stage build pour l'API Spring Boot dans pdfapi/
FROM maven:3.8.1-openjdk-8 AS build

WORKDIR /app

# Copier le projet complet
COPY . .

WORKDIR /app/pdfapi

# Utiliser Maven directement au lieu du wrapper
RUN mvn -B -Dmaven.test.skip=true dependency:resolve
RUN mvn -B -Dmaven.test.skip=true clean compile
RUN mvn -B -Dmaven.test.skip=true package

# Runtime image
FROM openjdk:8-jre
WORKDIR /app
COPY --from=build /app/pdfapi/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]