FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/hacking-puzzle-game-supabase-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]