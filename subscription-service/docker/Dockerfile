# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first to leverage Docker cache
RUN mvn dependency:go-offline -B
COPY src ./src
# Build the application, skipping tests to avoid DB connection issues during build
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /srv
# Copy only the built JAR from the builder stage
COPY --from=build /app/target/*.jar /srv/subscription-service.jar
COPY docker/subscription-service.sh /srv/
RUN chmod +x /srv/subscription-service.sh

EXPOSE 8080
ENTRYPOINT ["/srv/subscription-service.sh"]
