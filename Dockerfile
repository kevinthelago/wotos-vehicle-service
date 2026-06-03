# syntax=docker/dockerfile:1
# Multi-stage build: compile with Maven + JDK 17, run on a slim JRE 17.

FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
# Cache dependencies first (pom only), then build.
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src/ src/
RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy AS runtime
WORKDIR /app
# Non-root runtime user.
RUN useradd --system --uid 10001 wotos
COPY --from=build /workspace/target/*.jar app.jar
USER wotos
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
