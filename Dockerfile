FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat build.gradle settings.gradle gradle.properties ./
COPY gradle gradle
COPY src src

RUN chmod +x gradlew && ./gradlew --no-daemon bootJar

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}"]
