FROM openjdk:21 as builder
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bot.jar
RUN java -Djarmode=layertools -jar bot.jar extract

FROM openjdk:21
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
