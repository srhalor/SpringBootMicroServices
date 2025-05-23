# Use Amazon Corretto JDK 21 (Alpine) as the base image
FROM amazoncorretto:21-alpine3.21-jdk

# Set a non-root user and group
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set the working directory
WORKDIR /app

# Copy the jar file (built by Maven) into the container
COPY target/spring-jpa-demo-*.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

