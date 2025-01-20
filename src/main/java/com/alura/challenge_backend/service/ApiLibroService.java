package com.alura.challenge_backend.service;

import com.alura.challenge_backend.dto.AutorDTO;
import com.alura.challenge_backend.dto.LibroDTO;
import com.alura.challenge_backend.model.Autor;
import com.alura.challenge_backend.model.Idioma;
import com.alura.challenge_backend.model.Libro;
import com.alura.challenge_backend.repository.AutorRepository;
import com.alura.challenge_backend.repository.LibroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class ApiLibroService {

    private static final Logger logger = LoggerFactory.getLogger(ApiLibroService.class);

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private ApiUtilService apiUtilService;

    private static final String API_URL = "https://gutendex.com/books";

    public List<LibroDTO> buscarLibros(String queryParams) throws IOException, InterruptedException {
        logger.info("Buscando libros con query: {}", queryParams);
        HttpResponse<String> response = apiUtilService.enviarSolicitud(API_URL + "?search=" + queryParams, "GET", null);
        apiUtilService.validarRespuesta(response, 200);
        return apiUtilService.procesarLibros(response.body());
    }

    public List<LibroDTO> buscarLibrosPorTitulo(String titulo) throws IOException, InterruptedException {
        logger.info("Buscando libros con título: {}", titulo);
        HttpResponse<String> response = apiUtilService.enviarSolicitud(API_URL + "?search=" + titulo, "GET", null);
        apiUtilService.validarRespuesta(response, 200);
        return apiUtilService.procesarLibros(response.body());
    }

    public List<LibroDTO> filtrarLibrosPorIdioma(String idioma) throws IOException, InterruptedException {
        logger.info("Filtrando libros por idioma: {}", idioma);
        return apiUtilService.procesarLibrosPorIdioma(idioma);
    }

    public List<LibroDTO> listarTodosLosLibros() throws IOException, InterruptedException {
        logger.info("Listando todos los libros");
        HttpResponse<String> response = apiUtilService.enviarSolicitud(API_URL, "GET", null);
        apiUtilService.validarRespuesta(response, 200);
        return apiUtilService.procesarLibros(response.body());
    }

    public LibroDTO agregarLibro(LibroDTO libroDTO) {
        Libro libro = new Libro();
        libro.setTitulo(libroDTO.getTitulo());
        libro.setIdioma(Idioma.fromCodigo(libroDTO.getIdioma()));
        libro.setNumeroDescargas(libroDTO.getNumeroDescargas());
        libro.setAnioPublicacion(libroDTO.getAnioPublicacion());

        // Separar el nombre completo del autor
        String[] nombreCompleto = libroDTO.getAutor().getNombre().split(",");
        String apellido = nombreCompleto[0].trim();
        String nombre = nombreCompleto.length > 1 ? nombreCompleto[1].trim() : "";

        // Buscar o crear autor
        Autor autor = autorRepository.findByNombreAndApellido(nombre, apellido)
                .orElseGet(() -> {
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(nombre);
                    nuevoAutor.setApellido(apellido);
                    nuevoAutor.setAnioNacimiento(libroDTO.getAutor().getAnioNacimiento());
                    nuevoAutor.setAnioFallecimiento(libroDTO.getAutor().getAnioFallecimiento());
                    return autorRepository.save(nuevoAutor); // Guardar autor nuevo
                });

        // Asociar el autor gestionado al libro
        libro.setAutor(autor);

        // Guardar el libro
        libro = libroRepository.save(libro);

        // Retornar el DTO actualizado
        return new LibroDTO(libro.getId(), libro.getTitulo(), libro.getIdioma().getDescripcion(),
                libro.getNumeroDescargas(), libro.getAnioPublicacion(),
                new AutorDTO(libro.getAutor().getNombre(), libro.getAutor().getAnioNacimiento(), libro.getAutor().getAnioFallecimiento()));
    }




    public void eliminarLibroPorId(Long id) throws IOException, InterruptedException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del libro debe ser mayor a 0.");
        }

        String fullUrl = API_URL + "/" + id;
        logger.info("Eliminando libro con ID: {}", id);

        HttpResponse<String> response = apiUtilService.enviarSolicitud(fullUrl, "DELETE", null);
        apiUtilService.validarRespuesta(response, 204);
        logger.info("Libro eliminado exitosamente.");
    }
}
