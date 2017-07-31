FROM openjdk:8-jre

WORKDIR /data/ontology-service
ADD config.yml .
ADD target/service.jar .
ADD init.sh .

EXPOSE 8080 8081

CMD sh init.sh