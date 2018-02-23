FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} akkademo.jar
ADD run-app.sh /
RUN chmod +x run-app.sh
ENTRYPOINT ["/run-app.sh"]