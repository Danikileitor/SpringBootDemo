# Usamos una imagen base de Java (en este caso OpenJDK 17)
FROM openjdk:21-jdk-slim AS build

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo pom.xml y descargamos las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente del proyecto
COPY src ./src

# Construimos el archivo JAR usando Maven
RUN mvn clean package -DskipTests

# Usamos una imagen más ligera para ejecutar la aplicación
FROM openjdk:21-jdk-slim

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo JAR generado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Configura la variable de entorno para MongoDB
ENV MONGO_URI=mongodb://localhost:27017/

# Exponemos el puerto en el que la app Spring Boot escucha (por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
