#FROM openjdk:17-jdk-alpine
#
##Create user to run app as user instead of root
#RUN addgroup -S app && adduser -S app -G app
#
##Use user "app"
#USER app
#
##Copy the jar file into the docker image
#COPY target/app.jar app.jar
#
##Run the jar file
#ENTRYPOINT ["java", "-jar", "/app.jar"]

FROM openjdk:21
WORKDIR /app

COPY target/quarkus-app/ /app/

EXPOSE 8080

CMD ["java", "-jar", "/app/quarkus-run.jar"]