FROM openjdk:17
WORKDIR app
ADD /target/AndersenHotelProject-1.0-SNAPSHOT.jar back.jar
COPY /target/classes/ src/main/resources/
ENTRYPOINT ["java", "-jar", "back.jar"]