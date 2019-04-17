FROM openjdk:8
COPY . /usr/local/src/t1_distribuida/
WORKDIR /usr/local/src/t1_distribuida/
RUN ./gradlew clean fatJar
ENTRYPOINT ["java", "-jar", "build/libs/t1_distribuida.jar"]
