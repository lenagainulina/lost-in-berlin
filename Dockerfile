
FROM openjdk:8-jdk-alpine
COPY /target/lostberlin-0.0.1-SNAPSHOT.jar lostberlin.jar
EXPOSE 8080
CMD ["java", "-jar", "lostberlin.jar"]

