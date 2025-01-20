

```markdown
# LiterAlura

LiterAlura es una aplicación construida con Spring Boot para gestionar un catálogo de libros y autores utilizando la API de Gutendex como fuente de datos principal. Este proyecto permite buscar libros por título, filtrar por idioma, gestionar información de autores y almacenar datos en una base de datos PostgreSQL.

---

## Requisitos del sistema

- **Java JDK**: Versión 17 o superior  
  [Descargar Java JDK](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)

- **Maven**: Versión 4 o superior  
  [Maven](https://maven.apache.org/download.cgi)

- **Spring Boot**: Versión 3.2.3  
  [Spring Initializr](https://start.spring.io/)

- **PostgreSQL**: Versión 16 o superior  
  [Descargar PostgreSQL](https://www.postgresql.org/download/)

- **IDE**: (Opcional) IntelliJ IDEA  
  [Descargar IntelliJ IDEA](https://www.jetbrains.com/es-es/idea/download/?section=windows)

---

## Configuración del proyecto

### Dependencias de Maven

Asegúrate de que el archivo `pom.xml` incluya las siguientes dependencias:

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Jackson para procesamiento JSON -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.0</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>

    <!-- Pruebas -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

## Scripts para la base de datos
Inicializar la base de datos ejecutando los scripts SQL proporcionados en un cliente PostgreSQL.

Ejecuta los siguientes scripts para inicializar la base de datos:

```sql
-- Crear la base de datos
CREATE DATABASE literalura;

-- Conectarse a la base de datos literalura
\c literalura

-- Crear el esquema público (generalmente ya existe por defecto)
CREATE SCHEMA IF NOT EXISTS public;

-- Crear la tabla autores
CREATE TABLE public.autores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    fecha_nacimiento DATE,
    fecha_fallecimiento DATE,
    nacionalidad VARCHAR(100) DEFAULT 'Sin información',
    anio_fallecimiento INTEGER,
    anio_nacimiento INTEGER
);

-- Crear la secuencia para la tabla autores
CREATE SEQUENCE public.autores_id_seq
    AS INTEGER
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.autores_id_seq OWNED BY public.autores.id;

ALTER TABLE ONLY public.autores ALTER COLUMN id SET DEFAULT nextval('public.autores_id_seq'::regclass);

-- Crear la tabla libros
CREATE TABLE public.libros (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(500) NOT NULL,
    autor_id BIGINT NOT NULL,
    anio_publicacion INTEGER,
    genero VARCHAR(100),
    idioma VARCHAR(50) NOT NULL,
    numero_descargas INTEGER DEFAULT 0,
    CONSTRAINT libros_anio_publicacion_check CHECK (anio_publicacion > 0)
);

-- Crear la secuencia para la tabla libros
CREATE SEQUENCE public.libros_id_seq
    AS INTEGER
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.libros_id_seq OWNED BY public.libros.id;

ALTER TABLE ONLY public.libros ALTER COLUMN id SET DEFAULT nextval('public.libros_id_seq'::regclass);

-- Añadir la clave foránea (foreign key) para la tabla libros
ALTER TABLE ONLY public.libros
    ADD CONSTRAINT libros_autor_id_fkey FOREIGN KEY (autor_id) REFERENCES public.autores(id);
```

---

## Configuración de variables de entorno en Windows (Paso a Paso)

Para manejar información sensible como contraseñas o claves secretas, puedes configurar variables de entorno siguiendo estos pasos:

### Opción 1: Configuración temporal usando CMD o PowerShell

1. **Abrir CMD o PowerShell**:

   - Presiona `Windows + R`, escribe `cmd` o `powershell` y presiona Enter.

2. **Definir las variables de entorno**:

   - En **CMD**:
     ```cmd
     set DB_HOST=127.0.0.1
     set DB_PORT=5432
     set DB_USERNAME=postgres
     set DB_PASSWORD=secret
     set JWT_SECRET=my-secret-key
     ```
   - En **PowerShell**:
     ```powershell
     $Env:DB_HOST="127.0.0.1"
     $Env:DB_PORT="5432"
     $Env:DB_USERNAME="postgres"
     $Env:DB_PASSWORD="secret" (o una clave segura)
     $Env:JWT_SECRET="my-secret-key" (o una clave segura)
     ```

3. **Ejecutar la aplicación**:

   - Ejecuta el comando:
     ```cmd
     mvn spring-boot:run
     ```

4. **Nota**: Estas variables solo estarán disponibles mientras la ventana esté abierta.

### Opción 2: Configuración permanente desde el sistema

1. **Abrir la configuración del sistema**:

   - Haz clic derecho en el botón de inicio y selecciona `Sistema`.
   - Haz clic en **Configuración avanzada del sistema**.
   - Ve a la pestaña **Opciones avanzadas** y haz clic en **Variables de entorno...**.

2. **Agregar las variables**:

   - Haz clic en **Nueva...** en la sección de Variables del sistema.
   - Añade las siguientes variables una por una:
     - **Nombre**: `DB_HOST`, **Valor**: `127.0.0.1`
     - **Nombre**: `DB_PORT`, **Valor**: `5432`
     - **Nombre**: `DB_USERNAME`, **Valor**: `postgres`
     - **Nombre**: `DB_PASSWORD`, **Valor**: `secret`
     - **Nombre**: `JWT_SECRET`, **Valor**: `my-secret-key`

3. **Guardar los cambios**:

   - Haz clic en **Aceptar** en todas las ventanas.

4. **Reiniciar el sistema** (si es necesario):

   - Reinicia tu computadora para asegurar que los cambios se apliquen.

---

## Formas de ejecutar el proyecto

### Opción 1: Usando Maven

1. Navega a la carpeta raíz del proyecto en tu terminal.

2. Ejecuta el siguiente comando:

   ```bash
   mvn spring-boot:run
   ```

3. La aplicación estará disponible en `http://localhost:8080`.

### Opción 2: Ejecutando la clase principal desde un IDE

1. Abre el proyecto en tu IDE (por ejemplo, IntelliJ IDEA).
2. Localiza la clase `ChallengeBackendApplication` en el paquete `com.alura.challenge_backend`.
3. Haz clic derecho en el archivo y selecciona **Run 'ChallengeBackendApplication.main()'**.
4. La aplicación estará disponible en `http://localhost:8080`.

Código de la clase principal:

```java
package com.alura.challenge_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChallengeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChallengeBackendApplication.class, args);
    }
}
```

---

## Funcionalidades principales

1. **Búsqueda de libros por título**: Realiza consultas a la API de Gutendex.
2. **Gestión de libros y autores**: Almacena y gestiona información en una base de datos.
3. **Filtrado por idioma**: Permite filtrar libros por el idioma especificado.
4. **Listar autores vivos en un año específico**: Consulta a autores vivos con base en un año dado.
5. **Estadísticas**: Muestra la cantidad de libros por idioma almacenados en la base de datos.

---

## Autor

Jaime Rossi Serrano
```

