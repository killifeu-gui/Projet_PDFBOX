# Multi-stage build pour l'API Spring Boot dans pdfapi/
FROM maven:3.8.1-openjdk-8 AS build

WORKDIR /app

# Copier le projet complet (pdfapi/ + src/CalculatriceApp pour les stubs CORBA)
COPY . .

WORKDIR /app/pdfapi

# Build Maven avec skip tests et DLL de dépendances
RUN mvn -B \
    -Dmaven.test.skip=true \
    -DskipTests \
    -Dorg.slf4j.simpleLogger.defaultLogLevel=warn \
    clean package

# Runtime image (jre léger)
FROM eclipse-temurin:8-jre

WORKDIR /app

# Copier uniquement le JAR de l'application
COPY --from=build /app/pdfapi/target/*.jar /app/app.jar

# Expose le port Spring Boot
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]