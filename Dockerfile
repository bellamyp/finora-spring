# Stage 1: Build (Complile and package the application)
FROM maven:3.9.3-eclipse-temurin-17 AS build
# Now the target folder is at /app/target
WORKDIR /app
# Copy everything in this repo to the image
# COPY <source> <destination>
COPY . .
# Build and package this project as a jar file (run tests)
RUN mvn clean package

# Stage 2: Runtime (actually running the application)
FROM eclipse-temurin:17-jre
WORKDIR /app
# COPY <source> <destination>
COPY --from=build /app/target/finora-spring-0.0.1-SNAPSHOT.jar finora.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "finora.jar"]