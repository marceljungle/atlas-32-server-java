# ── Dockerfile ────────────────────────────────────────────────────────────────
# Build a tiny JRE image that only contains your shaded Spring‑Boot jar
FROM eclipse-temurin:21-jre-alpine

# Copy the JAR built by Maven/Gradle
ARG JAR_FILE=atlas-32-boot/target/atlas-32-boot-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /app.jar

# Expose the port Spring Boot listens on
EXPOSE 4046

# Start the application
ENTRYPOINT ["java","-jar","/app.jar"]
