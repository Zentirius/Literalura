package com.alura.challenge_backend.service;

import com.alura.challenge_backend.dto.AutorDTO;
import com.alura.challenge_backend.dto.LibroDTO;
import com.alura.challenge_backend.model.Idioma;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiUtilService {

    private static final Logger logger = LoggerFactory.getLogger(ApiUtilService.class);
    private static final String API_URL = "https://gutendex.com/books";
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Método público para obtener el objectMapper
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public HttpResponse<String> enviarSolicitud(String url, String metodo, String body) throws IOException, InterruptedException {
        HttpClient client = crearHttpClient();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(35)) // Ajustar el tiempo de espera a 35 segundos
                .header("Accept", "application/json");

        switch (metodo) {
            case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json");
            case "DELETE" -> requestBuilder.DELETE();
            default -> requestBuilder.GET();
        }

        HttpRequest request = requestBuilder.build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    public void validarRespuesta(HttpResponse<String> response, int... expectedStatusCodes) {
        for (int status : expectedStatusCodes) {
            if (response.statusCode() == status) {
                return;  // Respuesta válida
            }
        }
        throw new IllegalStateException("Error en la API: Código " + response.statusCode());
    }

    public List<LibroDTO> procesarLibros(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode results = root.get("results");

        List<LibroDTO> libros = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode node : results) {
                LibroDTO libro = new LibroDTO();
                libro.setId(node.get("id").asLong());
                libro.setTitulo(node.get("title").asText());

                if (node.hasNonNull("authors") && node.get("authors").isArray()) {
                    JsonNode authorNode = node.get("authors").get(0);
                    if (authorNode != null && authorNode.has("name")) {
                        AutorDTO autor = new AutorDTO();
                        autor.setNombre(authorNode.get("name").asText());
                        if (authorNode.hasNonNull("birth_year")) {
                            autor.setAnioNacimiento(authorNode.get("birth_year").asInt());
                        }
                        if (authorNode.hasNonNull("death_year")) {
                            autor.setAnioFallecimiento(authorNode.get("death_year").asInt());
                        }
                        libro.setAutor(autor);
                    } else {
                        System.out.println("No se encontró información del autor para este libro.");
                    }
                } else {
                    System.out.println("No se encontró información del autor para este libro.");
                }

                String idiomaString = node.hasNonNull("languages") && node.get("languages").isArray()
                        ? node.get("languages").get(0).asText()
                        : "Desconocido";

                try {
                    Idioma idioma = Idioma.fromCodigo(idiomaString);
                    libro.setIdioma(idioma.getDescripcion());
                } catch (IllegalArgumentException e) {
                    System.out.println("Idioma no reconocido: " + idiomaString);
                    libro.setIdioma("Desconocido");
                }

                libro.setNumeroDescargas(node.hasNonNull("download_count") ? node.get("download_count").asInt() : 0);
                libros.add(libro);
            }
        } else {
            logger.warn("No se encontraron resultados en la respuesta de la API.");
        }

        return libros;
    }

    public List<AutorDTO> procesarAutores(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        JsonNode results = root.get("results");

        List<AutorDTO> autores = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode node : results) {
                if (node.hasNonNull("authors")) {
                    for (JsonNode authorNode : node.get("authors")) {
                        if (authorNode != null && authorNode.has("name")) {
                            AutorDTO autor = new AutorDTO();
                            autor.setNombre(authorNode.get("name").asText());
                            if (authorNode.hasNonNull("birth_year")) {
                                autor.setAnioNacimiento(authorNode.get("birth_year").asInt());
                            }
                            if (authorNode.hasNonNull("death_year")) {
                                autor.setAnioFallecimiento(authorNode.get("death_year").asInt());
                            }
                            autores.add(autor);
                        } else {
                            System.out.println("No se encontró información del autor para este autor.");
                        }
                    }
                }
            }
        } else {
            logger.warn("No se encontraron resultados en la respuesta de la API.");
        }

        return autores;
    }

    private HttpClient crearHttpClient() {
        try {
            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .sslContext(SSLContext.getDefault())
                    .connectTimeout(Duration.ofSeconds(35))
                    .build();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error al configurar SSLContext", e);
            throw new RuntimeException("Error al configurar SSLContext", e);
        }
    }

    public long contarLibrosPorIdioma(String codigo) throws IOException, InterruptedException {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del idioma no puede ser nulo o estar vacío.");
        }

        String fullUrl = "https://gutendex.com/books" + "?languages=" + URLEncoder.encode(codigo.toLowerCase(), StandardCharsets.UTF_8);
        logger.info("Enviando solicitud a la API para contar libros por idioma: {}", fullUrl);

        HttpResponse<String> response = enviarSolicitud(fullUrl, "GET", null);
        validarRespuesta(response, 200);

        JsonNode root = new ObjectMapper().readTree(response.body());

        // Obtener el valor del campo "count"
        long count = root.path("count").asLong();

        // Añadir logs para verificar la respuesta
        logger.info("Respuesta de la API: {}", response.body());
        logger.info("Número de resultados (count): {}", count);

        JsonNode results = root.get("results");
        if (results != null) {
            logger.info("Número de elementos en 'results': {}", results.size());
            results.forEach(result -> {
                String title = result.path("title").asText();
                logger.info("Título: {}", title);
            });
        } else {
            logger.warn("No se encontraron resultados en la respuesta de la API.");
        }

        return count;
    }

    public List<LibroDTO> obtenerLibrosPorIdioma(String codigo) throws IOException, InterruptedException {
        String fullUrl = "https://gutendex.com/books" + "?languages=" + URLEncoder.encode(codigo.toLowerCase(), StandardCharsets.UTF_8);
        logger.info("Enviando solicitud a la API para obtener libros por idioma: {}", fullUrl);

        HttpResponse<String> response = enviarSolicitud(fullUrl, "GET", null);
        validarRespuesta(response, 200);

        JsonNode root = new ObjectMapper().readTree(response.body());
        JsonNode results = root.path("results");

        List<LibroDTO> libros = new ArrayList<>();
        if (results != null) {
            results.forEach(result -> {
                LibroDTO libro = new LibroDTO();
                libro.setId(result.path("id").asLong());
                libro.setTitulo(result.path("title").asText());
                libro.setIdioma(codigo);
                libro.setNumeroDescargas(result.path("download_count").isNull() ? null : result.path("download_count").asInt());
                libro.setAnioPublicacion(result.path("publish_year").isNull() ? null : result.path("publish_year").asInt());

                // Verificar si el nodo 'authors' existe y no es nulo
                JsonNode authorsNode = result.path("authors");
                if (authorsNode != null && authorsNode.size() > 0) {
                    JsonNode authorNode = authorsNode.get(0);
                    AutorDTO autor = new AutorDTO(
                            authorNode.path("name").asText(),
                            authorNode.path("birth_year").isNull() ? null : authorNode.path("birth_year").asInt(),
                            authorNode.path("death_year").isNull() ? null : authorNode.path("death_year").asInt()
                    );
                    libro.setAutor(autor);
                } else {
                    libro.setAutor(new AutorDTO("Información no disponible"));
                }

                libros.add(libro);
            });

            // Añadir logs para verificar los resultados
            logger.info("Número de resultados: {}", libros.size());
            libros.forEach(libro -> {
                logger.info("ID: {}, Título: {}, Autor: {}", libro.getId(), libro.getTitulo(), libro.getAutor().getNombre());
            });
        } else {
            logger.warn("No se encontraron resultados en la respuesta de la API.");
        }

        return libros;
    }



    public List<LibroDTO> procesarLibrosPorIdioma(String codigo) throws IOException, InterruptedException {
        String fullUrl = "https://gutendex.com/books" + "?languages=" + URLEncoder.encode(codigo.toLowerCase(), StandardCharsets.UTF_8);
        logger.info("Enviando solicitud a la API para procesar libros por idioma: {}", fullUrl);

        HttpResponse<String> response = enviarSolicitud(fullUrl, "GET", null);
        validarRespuesta(response, 200);

        JsonNode root = new ObjectMapper().readTree(response.body());
        JsonNode results = root.path("results");

        List<LibroDTO> libros = new ArrayList<>();
        if (results != null) {
            results.forEach(result -> {
                LibroDTO libro = new LibroDTO();
                libro.setId(result.path("id").asLong());
                libro.setTitulo(result.path("title").asText());
                libro.setIdioma(codigo);
                libro.setNumeroDescargas(result.path("download_count").isNull() ? null : result.path("download_count").asInt());
                libro.setAnioPublicacion(result.path("publish_year").isNull() ? null : result.path("publish_year").asInt());

                // Verificar si el nodo 'authors' existe y no es nulo
                JsonNode authorsNode = result.path("authors");
                if (authorsNode != null && authorsNode.size() > 0) {
                    JsonNode authorNode = authorsNode.get(0);
                    AutorDTO autor = new AutorDTO(
                            authorNode.path("name").asText(),
                            authorNode.path("birth_year").isNull() ? null : authorNode.path("birth_year").asInt(),
                            authorNode.path("death_year").isNull() ? null : authorNode.path("death_year").asInt()
                    );
                    libro.setAutor(autor);
                } else {
                    libro.setAutor(new AutorDTO("Información no disponible"));
                }

                libros.add(libro);
            });

            // Añadir logs para verificar los resultados
            logger.info("Número de resultados: {}", libros.size());
            libros.forEach(libro -> {
                logger.info("ID: {}, Título: {}, Autor: {}", libro.getId(), libro.getTitulo(), libro.getAutor().getNombre());
            });
        } else {
            logger.warn("No se encontraron resultados en la respuesta de la API.");
        }

        return libros;
    }

}



