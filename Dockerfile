FROM openjdk:11
ARG JAR_FILE=/target/exchangeRates-*.jar

VOLUME /tmp
WORKDIR /app
COPY ${JAR_FILE} /app/exchangeRates.jar
EXPOSE 8080

CMD ["java", "-jar", "exchangeRates.jar"]