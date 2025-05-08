FROM openjdk:17-slim
COPY build/libs/Rewear-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]