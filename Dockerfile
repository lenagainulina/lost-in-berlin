FROM openjdk:8
ADD Main.jar Main.jar
WORKDIR /tmp
EXPOSE 8080
ENTRYPOINT ["java","LostberlinApplication"]