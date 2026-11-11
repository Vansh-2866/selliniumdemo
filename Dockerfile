FROM markhobson/maven-chrome:jdk-17
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
CMD ["mvn", "test", "-Dheadless=true"]
