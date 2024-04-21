FROM openjdk:21 as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} scrapper.jar
RUN java -Djarmode=layertools -jar scrapper.jar extract

FROM openjdk:21
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
