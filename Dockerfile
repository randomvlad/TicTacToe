# syntax=docker/dockerfile:1

##############################
## Build stage
##############################
FROM eclipse-temurin:27-jdk@sha256:5e6d9093c7a8e95adfaa8828253c58f2c6820116d07a062440a778358807578a AS build
WORKDIR /workspace

# Resolve dependencies first so this layer is cached until the build files change
COPY gradlew settings.gradle build.gradle ./
COPY gradle/ gradle/
RUN ./gradlew --no-daemon dependencies > /dev/null

# Build the executable jar (tests run in CI, see .github/workflows/gradle.yml)
COPY src/ src/
RUN ./gradlew --no-daemon bootJar -x test \
    && find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} app.jar \;

##############################
## Runtime stage
##############################
FROM eclipse-temurin:27-jre@sha256:acfb350b50e707addf3dcbb47f7052d1882d1e5f1670ce838a9c7838d82d4fe5
WORKDIR /app

RUN useradd --system --no-create-home --shell /usr/sbin/nologin app
COPY --from=build --chown=app:app /workspace/app.jar app.jar
USER app

# Use an in-memory H2 database inside the container and keep logs free of SQL noise
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:tictactoe \
    SPRING_JPA_PROPERTIES_HIBERNATE_SHOW_SQL=false

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
