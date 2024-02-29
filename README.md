
# BCI - API de usuarios
## Cómo hacerlo andar
### Servidor
- Descargar [Java 17](https://adoptium.net/temurin/releases/?version=17)
- Descargar [Apache Maven](https://maven.apache.org/download.cgi)

- Establecer JAVA_HOME en la ubicación de java 17 
	- LINUX: `export JAVA_HOME=path/to/java/17`
	- WIN: `$env:JAVA_HOME=path\to\java\17`

- `mvn clean install` en el root del proyecto para descargar las dependencias necesarias

- `mvn spring-boot:run` en el root de proyecto de backend.
El server se iniciará en http://localhost:8005

### Base de datos
La base de datos es H2 en memoria. La misma se recrea con cada reinicio del servidor.
Se utiliza LIQUIBASE para el armado automático del esquema de DB a partir de un script que se encuentra en `$projectRoot/src/main/resources/db/changelog/changes/001-initial-schema.sql`
El msmo puede ejecutarse para la creación manual del esquema de base de datos.

## Cómo probarlo

### Swagger
Se implementó Swagger/Open API para facilitar el testeo de la API.
Pueden accederlo desde http://localhost:8005/swagger-ui/index.html

### Postman
Se adjunta una colección de Postman en este mismo repositorio (`$projectRoot/BCI.postman_collection.json`). 
El mismo cuenta con funcionalidad para guardar automáticamente el token generado en el registro de usuario, y requerido por los otros endpoints.