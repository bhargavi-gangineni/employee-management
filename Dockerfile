FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/employee-management-1.0.0.jar app.jar

CMD ["java", "-cp", "app.jar", "com.example.employee.WebServer"]