FROM openjdk:8-jre

WORKDIR /data/webprotege-rest-api
ADD config.yml .
ADD service.jar .

EXPOSE 8080 8081

CMD [ 'sh', 'init.sh' ]