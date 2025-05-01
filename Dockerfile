# --- Stage 1: Build the application ---
FROM openjdk:21 AS builder

WORKDIR /code-sprout-api

# Copy build files first
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY .mvn .mvn
COPY pom.xml pom.xml

# Give permission to mvnw
RUN chmod +x mvnw

# Download dependencies (cached if pom.xml unchanged)
RUN ./mvnw dependency:go-offline

# Now copy source code
COPY src src

# Package the application
RUN ./mvnw package -DskipTests

# --- Stage 2: Create the minimal runtime image ---
FROM openjdk:21

WORKDIR /code-sprout-api

# Only copy the final jar from builder stage
COPY --from=builder /code-sprout-api/target/code-sprout-api.jar app.jar

EXPOSE 9093

# Start the application
CMD ["java", "-jar", "app.jar"]

#docker run --env-file .env -p 9093:9093 code-sprout-api