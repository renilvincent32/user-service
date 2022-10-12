FROM adoptopenjdk/openjdk8
MAINTAINER renilvincent
COPY target/user-service-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]