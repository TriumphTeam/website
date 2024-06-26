FROM eclipse-temurin:21-jre-jammy

ADD docs/build/libs/docs.jar /app/application.jar

WORKDIR /app
CMD ["java", "-jar", "/app/application.jar"]
