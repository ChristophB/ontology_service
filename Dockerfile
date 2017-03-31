FROM openjdk:8-jre

WORKDIR /data/webprotege-rest-api
ADD config.yml .
ADD service.jar .

EXPOSE 8080 8081

CMD java -jar service.jar server config.yml