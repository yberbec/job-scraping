
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

VOLUME /tmp

# Create the directory for exported Excel files
RUN mkdir -p /app/output

# Configure the output directory for the Java application
ENV OUTPUT_DIR=/app/output

COPY target/job-scraping-app-1.0-SNAPSHOT.jar /app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]
