FROM openjdk:21

# Configura la variable de entorno para la aplicación
ENV APP_NAME=demo
WORKDIR /app

# Copia los archivos de tu proyecto a la imagen
COPY target/demo.jar /app/demo.jar

# Configura la variable de entorno para MongoDB
ENV MONGO_URI=mongodb://localhost:27017/

# Expones el puerto 8080 para que se pueda acceder a la aplicación
EXPOSE 8080

# Define el comando que se ejecutará al iniciar el contenedor
CMD ["java", "-jar", "/app/demo.jar"]
