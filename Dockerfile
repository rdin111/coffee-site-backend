# --- Stage 1: The Build Stage ---
# Use a full JDK image to build the application
FROM openjdk:17-jdk-slim as builder

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle gradle

# Copy the build configuration file
COPY build.gradle .
COPY settings.gradle .

# Copy the rest of the source code
COPY src src

# Make the Gradle wrapper script executable
RUN chmod +x ./gradlew

# Run the build but explicitly exclude the test task
RUN ./gradlew build -x test --no-daemon


# --- Stage 2: The Final Image Stage ---
# Use the same slim JRE image for a small final image size
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy ONLY the built jar file from the 'builder' stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the port and run the application
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]