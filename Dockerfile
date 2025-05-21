FROM openjdk:21-jdk-slim

WORKDIR /app

COPY wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
COPY target/analytic-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["./wait-for-it.sh", "postgres_analytics_db:5433", "--", "java", "-jar", "app.jar"]