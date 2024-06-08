FROM openjdk:17

RUN mkdir /app

COPY backend.jar /app

WORKDIR /app

CMD java -Xms128M -Xmx1024M -jar backend.jar