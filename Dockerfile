# ------------------------------------------
# Stage 1: Build the application
# ------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Pre-copy POM and wrapper to cache deps first
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Give executable permission to mvnw
RUN chmod +x mvnw

# Download dependencies first (cache-friendly)
RUN ./mvnw dependency:go-offline

# Copy rest of the source code
COPY src src

# Build the application (skip tests)
RUN ./mvnw clean package -DskipTests

# ------------------------------------------
# Stage 2: Run the application
# ------------------------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the final built JAR from builder
COPY --from=builder /build/target/code-sprout-api.jar .

EXPOSE 9093

CMD ["java", "-jar", "code-sprout-api.jar"]

#docker run --env-file .env -p 9093:9093 code-sprout-api