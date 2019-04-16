FROM openjdk:8
COPY . /usr/src/t1_distribuida
WORKDIR /usr/src/t1_distribuida
RUN ./gradlew build
WORKDIR /usr/src/t1_distribuida/build/libs
ENTRYPOINT ["java", "-jar", "t1_distribuida.jar"]
