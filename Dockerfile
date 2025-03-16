FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ExpenseTrackerBot-1.0-SNAPSHOT.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]
