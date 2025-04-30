FROM gradle:7.6-jdk21 AS builder
WORKDIR /src

COPY gradlew               ./
COPY gradle/wrapper        gradle/wrapper/
COPY build.gradle          ./
COPY settings.gradle       ./
RUN chmod +x gradlew

RUN ./gradlew --no-daemon dependencies || true

COPY src/                  src/
RUN ./gradlew clean bootJar --no-daemon --console=plain --stacktrace --build-cache

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder /src/build/libs/*.jar ./app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
