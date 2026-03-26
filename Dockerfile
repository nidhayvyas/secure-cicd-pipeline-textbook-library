FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/textbook-library-6.7.6.jar app.jar

EXPOSE 3001

ENTRYPOINT ["java", "-jar", "app.jar"]
