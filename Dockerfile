FROM openjdk:21 AS build

# Configura la variable de entorno para la aplicación
ENV APP_NAME=demo

# Genera la build
RUN mvn clean package
RUN ./mvnw spring-boot:build-image

FROM openjdk:21

# Copia los archivos de tu proyecto a la imagen
COPY target/demo.jar /app/

# Configura la variable de entorno para MongoDB
ENV MONGO_URI=mongodb://localhost:27017/

# Expones el puerto 8080 para que se pueda acceder a la aplicación
EXPOSE 8080

# Define el comando que se ejecutará al iniciar el contenedor
CMD ["java", "-jar", "/app/demo.jar"]
