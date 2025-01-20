# LiterAlura

LiterAlura es una aplicación construida con **Spring Boot** para gestionar un catálogo de libros y autores utilizando la API de **Gutendex** como fuente de datos principal. Este proyecto permite:

- Buscar libros por título.
- Filtrar por idioma.
- Gestionar información de autores.
- Almacenar datos en una base de datos **PostgreSQL**.

---

## 🚀 Requisitos del Sistema

### Software Necesario:

- **Java JDK**: Versión 17 o superior  
  [Descargar Java JDK](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)

- **Maven**: Versión 4 o superior  
  [Descargar Maven](https://maven.apache.org/download.cgi)

- **Spring Boot**: Versión 3.2.3  
  [Spring Initializr](https://start.spring.io/)

- **PostgreSQL**: Versión 16 o superior  
  [Descargar PostgreSQL](https://www.postgresql.org/download/)

- **IDE (opcional)**: IntelliJ IDEA  
  [Descargar IntelliJ IDEA](https://www.jetbrains.com/es-es/idea/download/?section=windows)

---

## ⚙️ Configuración del Proyecto

### Dependencias de Maven

Asegúrate de incluir las siguientes dependencias en tu archivo `pom.xml`:

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

## 📂 Scripts para la Base de Datos

### Inicialización de la Base de Datos

Ejecuta los siguientes scripts en tu cliente PostgreSQL para crear las tablas necesarias:

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

## 🔒 Configuración de Variables de Entorno

Para manejar información sensible como contraseñas, utiliza variables de entorno:

### Configuración Temporal

En **CMD**:
```cmd
set DB_HOST=127.0.0.1
set DB_PORT=5432
set DB_USERNAME=postgres
set DB_PASSWORD=secret
set JWT_SECRET=my-secret-key
```

En **PowerShell**:
```powershell
$Env:DB_HOST="127.0.0.1"
$Env:DB_PORT="5432"
$Env:DB_USERNAME="postgres"
$Env:DB_PASSWORD="secret"
$Env:JWT_SECRET="my-secret-key"
```

### Configuración Permanente

1. Ve a **Configuración avanzada del sistema**.
2. Haz clic en **Variables de entorno...** y agrega las variables necesarias:
    - `DB_HOST`: `127.0.0.1`
    - `DB_PORT`: `5432`
    - `DB_USERNAME`: `postgres`
    - `DB_PASSWORD`: `secret`
    - `JWT_SECRET`: `my-secret-key`

---

## ▶️ Ejecución del Proyecto

### Opción 1: Usando Maven

1. Navega al directorio del proyecto.
2. Ejecuta:

   ```bash
   mvn spring-boot:run
   ```

3. Accede a la aplicación en: [http://localhost:8080](http://localhost:8080).

### Opción 2: Desde un IDE

1. Abre el proyecto en tu IDE.
2. Localiza la clase principal `ChallengeBackendApplication`.
3. Haz clic en **Run**.

---

## 🛠️ Funcionalidades Principales

1. **Búsqueda de libros por título**: Utilizando la API de Gutendex.
2. **Gestión de libros y autores**: Almacenamiento en la base de datos.
3. **Filtrado por idioma**.
4. **Listar autores vivos en un año específico**.
5. **Estadísticas**: Cantidad de libros por idioma.

---

## ✒️ Autor

Jaime Rossi Serrano
