//package com.alura.challenge_backend.service;
//
//import com.alura.challenge_backend.dto.AutorDTO;
//import com.alura.challenge_backend.dto.LibroDTO;
//import com.alura.challenge_backend.repository.AutorRepository;
//import com.alura.challenge_backend.repository.LibroRepository;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.net.ssl.SSLContext;
//import java.io.IOException;
//import java.net.URI;
//import java.net.URLEncoder;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.security.NoSuchAlgorithmException;
//
//@Service
//public class ApiService {
//    @Autowired
//    private LibroRepository libroRepository;
//    @Autowired
//    private AutorRepository autorRepository;
//
//
//    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);
//    private static final String API_URL = "https://gutendex.com/books";
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    /**
//     * Busca libros por título en la API.
//     */
//    public List<LibroDTO> buscarLibrosPorTitulo(String titulo) throws IOException, InterruptedException {
//        if (titulo == null || titulo.isBlank()) {
//            throw new IllegalArgumentException("El título no puede ser nulo o estar vacío.");
//        }
//
//        String fullUrl = API_URL + "?search=" + URLEncoder.encode(titulo, StandardCharsets.UTF_8);
//        logger.info("Enviando solicitud a la API para buscar libros por título: {}", fullUrl);
//
//        HttpResponse<String> response = enviarSolicitud(fullUrl, "GET", null);
//        validarRespuesta(response, 200);
//        return procesarLibros(response.body());
//    }
//
//    /**
//     * Filtra libros por idioma en la API.
//     */
//    public List<LibroDTO> filtrarLibrosPorIdioma(String idioma) throws IOException, InterruptedException {
//        if (idioma == null || idioma.isBlank()) {
//            throw new IllegalArgumentException("El idioma no puede ser nulo o estar vacío.");
//        }
//
//        String fullUrl = API_URL + "?languages=" + URLEncoder.encode(idioma, StandardCharsets.UTF_8);
//        logger.info("Enviando solicitud a la API para filtrar libros por idioma: {}", fullUrl);
//
//        HttpResponse<String> response = enviarSolicitud(fullUrl, "GET", null);
//        validarRespuesta(response, 200);
//        return procesarLibros(response.body());
//    }
//
//    /**
//     * Lista todos los libros disponibles en la API.
//     */
//    public List<LibroDTO> listarTodosLosLibros() throws IOException, InterruptedException {
//        logger.info("Enviando solicitud a la API para listar todos los libros.");
//        HttpResponse<String> response = enviarSolicitud(API_URL, "GET", null);
//        validarRespuesta(response, 200);
//        return procesarLibros(response.body());
//    }
//
//    /**
//     * Lista todos los autores disponibles en la API.
//     */
//    public List<AutorDTO> listarTodosLosAutores() throws IOException, InterruptedException {
//        logger.info("Enviando solicitud a la API para listar todos los autores.");
//        HttpResponse<String> response = enviarSolicitud(API_URL, "GET", null);
//        validarRespuesta(response, 200);
//        return procesarAutores(response.body());
//    }
//
//    /**
//     * Agrega un libro utilizando la API.
//     */
//    public LibroDTO agregarLibro(LibroDTO libroDTO) throws IOException, InterruptedException {
//        if (libroDTO == null) {
//            throw new IllegalArgumentException("El libro no puede ser nulo.");
//        }
//
//        String requestBody = objectMapper.writeValueAsString(libroDTO);
//        logger.info("Enviando solicitud a la API para agregar un libro.");
//
//        HttpResponse<String> response = enviarSolicitud(API_URL, "POST", requestBody);
//        validarRespuesta(response, 201); // Esperamos 201 (Created) para POST
//        return objectMapper.readValue(response.body(), LibroDTO.class);
//    }
//
//    /**
//     * Elimina un libro por ID utilizando la API.
//     */
//    public void eliminarLibroPorId(Long id) throws IOException, InterruptedException {
//        if (id == null || id <= 0) {
//            throw new IllegalArgumentException("El ID del libro debe ser mayor a 0.");
//        }
//
//        String fullUrl = API_URL + "/" + id;
//        logger.info("Enviando solicitud a la API para eliminar libro con ID: {}", id);
//
//        HttpResponse<String> response = enviarSolicitud(fullUrl, "DELETE", null);
//        validarRespuesta(response, 204); // Esperamos 204 (No Content) para DELETE
//        logger.info("Libro eliminado exitosamente.");
//    }
//
//    /**
//     * Lista autores vivos hasta el año dado.
//     */
//    public List<AutorDTO> listarAutoresVivos(int anioActual) throws IOException, InterruptedException {
//        if (anioActual <= 0) {
//            throw new IllegalArgumentException("El año debe ser un valor positivo.");
//        }
//
//        logger.info("Enviando solicitud a la API para listar autores hasta el año: {}", anioActual);
//        HttpResponse<String> response = enviarSolicitud(API_URL, "GET", null);
//        validarRespuesta(response);
//
//        List<AutorDTO> todosLosAutores = procesarAutores(response.body());
//        return todosLosAutores.stream()
//                .filter(autor -> autor.getAnioFallecimiento() == null || autor.getAnioFallecimiento() > anioActual)
//                .collect(Collectors.toList());  // Usamos > en lugar de isAfter porque anioFallecimiento es Integer
//    }
//    /**
//     * Envía una solicitud HTTP genérica a la API.
//     */
//    private HttpResponse<String> enviarSolicitud(String url, String metodo, String body) throws IOException, InterruptedException {
//        HttpClient client = crearHttpClient();
//        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("Accept", "application/json");
//
//        switch (metodo) {
//            case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body))
//                    .header("Content-Type", "application/json");
//            case "DELETE" -> requestBuilder.DELETE();
//            default -> requestBuilder.GET();
//        }
//
//        HttpRequest request = requestBuilder.build();
//        return client.send(request, HttpResponse.BodyHandlers.ofString());
//    }
//
//    /**
//     * Valida la respuesta HTTP para asegurar que fue exitosa.
//     */
//    private void validarRespuesta(HttpResponse<String> response, int... expectedStatusCodes) {
//        boolean isValid = false;
//        for (int status : expectedStatusCodes) {
//            if (response.statusCode() == status) {
//                isValid = true;
//                break;
//            }
//        }
//        if (!isValid) {
//            throw new IllegalStateException("Error en la API: Código " + response.statusCode());
//        }
//    }
//
//    /**
//     * Procesa la respuesta JSON de la API y la convierte en una lista de objetos LibroDTO.
//     */
//    private List<LibroDTO> procesarLibros(String json) throws IOException {
//        JsonNode root = objectMapper.readTree(json);
//        JsonNode results = root.get("results");
//
//        List<LibroDTO> libros = new ArrayList<>();
//        if (results != null && results.isArray()) {
//            for (JsonNode node : results) {
//                LibroDTO libro = new LibroDTO();
//                libro.setTitulo(node.get("title").asText());
//
//                if (node.hasNonNull("authors") && node.get("authors").isArray()) {
//                    JsonNode authorNode = node.get("authors").get(0);
//                    AutorDTO autor = new AutorDTO();
//                    autor.setNombre(authorNode.get("name").asText());
//                    if (authorNode.hasNonNull("birth_year")) {
//                        autor.setAnioNacimiento(authorNode.get("birth_year").asInt());
//                    }
//                    if (authorNode.hasNonNull("death_year")) {
//                        autor.setAnioFallecimiento(authorNode.get("death_year").asInt());
//                    }
//                    libro.setAutor(autor);
//                }
//
//                libro.setIdioma(node.hasNonNull("languages") && node.get("languages").isArray()
//                        ? node.get("languages").get(0).asText()
//                        : "Desconocido");
//                libro.setNumeroDescargas(node.hasNonNull("download_count") ? node.get("download_count").asInt() : 0);
//                libros.add(libro);
//            }
//        } else {
//            logger.warn("No se encontraron resultados en la respuesta de la API.");
//        }
//
//        return libros;
//    }
//
//    /**
//     * Procesa la respuesta JSON de la API y la convierte en una lista de objetos AutorDTO.
//     */
//    private List<AutorDTO> procesarAutores(String json) throws IOException {
//        JsonNode root = objectMapper.readTree(json);
//        JsonNode results = root.get("results");
//
//        List<AutorDTO> autores = new ArrayList<>();
//        if (results != null && results.isArray()) {
//            for (JsonNode node : results) {
//                if (node.hasNonNull("authors")) {
//                    for (JsonNode authorNode : node.get("authors")) {
//                        AutorDTO autor = new AutorDTO();
//                        autor.setNombre(authorNode.get("name").asText());
//                        if (authorNode.hasNonNull("birth_year")) {
//                            autor.setAnioNacimiento(authorNode.get("birth_year").asInt());
//                        }
//                        if (authorNode.hasNonNull("death_year")) {
//                            autor.setAnioFallecimiento(authorNode.get("death_year").asInt());
//                        }
//                        autores.add(autor);
//                    }
//                }
//            }
//        } else {
//            logger.warn("No se encontraron resultados en la respuesta de la API.");
//        }
//
//        return autores;
//    }
//
//    /**
//     * Crea una instancia de HttpClient con configuración estándar.
//     */
//    private HttpClient crearHttpClient() {
//        try {
//            return HttpClient.newBuilder()
//                    .version(HttpClient.Version.HTTP_2)
//                    .followRedirects(HttpClient.Redirect.ALWAYS)
//                    .sslContext(SSLContext.getDefault())
//                    .connectTimeout(Duration.ofSeconds(10))
//                    .build();
//        } catch (NoSuchAlgorithmException e) {
//            logger.error("Error al configurar SSLContext", e);
//            throw new RuntimeException("Error al configurar SSLContext", e);
//        }
//    }
//
//    public long contarLibrosPorIdioma(String codigo) throws IOException, InterruptedException {
//        if (codigo == null || codigo.isBlank()) {
//            throw new IllegalArgumentException("El código del idioma no puede ser nulo o estar vacío.");
//        }
//
//        String fullUrl = API_URL + "?languages=" + URLEncoder.encode(codigo, StandardCharsets.UTF_8);
//        logger.info("Enviando solicitud a la API para contar libros por idioma: {}", fullUrl);
//
//        HttpResponse<String> response = enviarSolicitud(fullUrl, "GET", null);
//        validarRespuesta(response, 200);
//
//        JsonNode root = objectMapper.readTree(response.body());
//        JsonNode results = root.get("results");
//        return results != null ? results.size() : 0;
//    }
//
//    @Transactional
//    public void eliminarTodosLosLibros() {
//        libroRepository.deleteAll();
//        autorRepository.deleteAll();
//        logger.info("Base de datos limpiada: todos los libros y autores han sido eliminados.");
//    }
//}