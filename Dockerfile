FROM eclipse-temurin:21

WORKDIR /app

COPY build/libs/debounce-v2-1.jar /app/app.jar

EXPOSE 8080
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
