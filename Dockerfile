# --- Stage 1: The Build Stage ---
FROM openjdk:17-jdk-slim as builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Make the Gradle wrapper script executable
RUN chmod +x ./gradlew

# Run the build but explicitly exclude the test task
RUN ./gradlew build -x test --no-daemon


# --- Stage 2: The Final Image Stage ---
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy ONLY the built jar file from the 'builder' stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]