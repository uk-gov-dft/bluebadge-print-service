FROM java:8-jre-alpine
ARG JAR_NAME
COPY "build/libs/${JAR_NAME}" "/usr/src/app/app.jar"
EXPOSE 8880 8881 8800
CMD ["java","-jar","/usr/src/app/app.jar"]
