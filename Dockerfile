# Use lightweight Java 17 image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy your built JAR file
COPY target/app-*.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]