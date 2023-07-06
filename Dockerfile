FROM openjdk:17

WORKDIR /app

COPY target/CHISoftwareTestTask.jar .

VOLUME /app/config

EXPOSE 8080

CMD ["java", "-jar", "CHISoftwareTestTask.jar", "--spring.config.location=file:/app/config/application.properties"]