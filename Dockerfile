# Use an OpenJDK 17 image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy Maven wrapper and dependencies first for caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY . .

# Build the Spring Boot app
RUN ./mvnw package -DskipTests

# Set environment variables (can also be passed via `docker run -e`)
ENV SPRING_PROFILES_ACTIVE=prod

# Expose the backend port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "target/DebtMates-BE-0.0.1-SNAPSHOT.jar"]