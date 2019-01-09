FROM openjdk:8-jre-stretch
ARG JAR_FILE
ADD ${JAR_FILE} app.jar
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.2.1/wait /wait
RUN chmod +x /wait && mkdir ~/.ssh && apt-get -yq update && apt-get -yqq install ssh
