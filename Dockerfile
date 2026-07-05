# ─────────────────────────────────────────────────────────────
# Stage 1: Build the Spring Boot JAR
# ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy dependency descriptor first (layer caching)
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─────────────────────────────────────────────────────────────
# Stage 2: Minimal runtime image
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Render injects PORT dynamically — default to 8080 if not set
EXPOSE 8080

# All secrets (DB URL, Gemini key, JWT secret) are injected as
# environment variables by Render at runtime — never hardcoded here.
ENTRYPOINT ["java", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
