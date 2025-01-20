package com.alura.challenge_backend.service;

import com.alura.challenge_backend.dto.AutorDTO;
import com.alura.challenge_backend.dto.LibroDTO;
import com.alura.challenge_backend.model.Autor;
import com.alura.challenge_backend.model.Idioma;
import com.alura.challenge_backend.model.Libro;
import com.alura.challenge_backend.repository.AutorRepository;
import com.alura.challenge_backend.repository.LibroRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private static final Logger logger = LoggerFactory.getLogger(LibroService.class);

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private ApiUtilService apiUtilService;

    @PersistenceContext
    private EntityManager entityManager;

    private static final String API_URL = "https://gutendex.com/books";

    public List<Libro> findAll() {
        return libroRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public void eliminarLibroPorId(Long id) {
        logger.info("Buscando libro con ID: {}", id);
        Optional<Libro> libro = libroRepository.findById(id);
        if (libro.isPresent()) {
            logger.info("Libro encontrado: {}", libro.get());
            libroRepository.delete(libro.get());
            libroRepository.flush();
            entityManager.clear();
            logger.info("Libro eliminado y contexto limpiado.");
        } else {
            logger.warn("Libro con ID {} no encontrado.", id);
        }
    }

    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    public List<Libro> listarTodosLosLibros() {
        return libroRepository.findAll();
    }

    public List<LibroDTO> buscarLibrosDesdeAPI(String queryParams) throws IOException, InterruptedException {
        HttpResponse<String> response = apiUtilService.enviarSolicitud(API_URL + "?search=" + queryParams, "GET", null);
        apiUtilService.validarRespuesta(response, 200);
        return apiUtilService.procesarLibros(response.body());
    }

    @Transactional
    public List<LibroDTO> mostrarTodosLosLibros() {
        List<Libro> libros = entityManager.createQuery("SELECT DISTINCT l FROM Libro l JOIN FETCH l.autor", Libro.class).getResultList();
        return libros.stream().map(libro -> new LibroDTO(
                libro.getId(),
                libro.getTitulo(),
                libro.getIdioma().getDescripcion(),
                libro.getNumeroDescargas(),
                libro.getAnioPublicacion(),
                new AutorDTO(libro.getAutor().getNombre(), libro.getAutor().getAnioNacimiento(), libro.getAutor().getAnioFallecimiento())
        )).collect(Collectors.toList());
    }


    @Transactional(rollbackFor = Exception.class)
    public Libro agregarLibro(LibroDTO libroDTO) {
        Libro libro = new Libro();
        libro.setTitulo(libroDTO.getTitulo());
        libro.setIdioma(Idioma.fromCodigo(libroDTO.getIdioma()));
        libro.setNumeroDescargas(libroDTO.getNumeroDescargas());
        libro.setAnioPublicacion(libroDTO.getAnioPublicacion());

        String[] nombreCompleto = libroDTO.getAutor().getNombre().split(",");
        String apellido = nombreCompleto[0].trim();
        String nombre = nombreCompleto.length > 1 ? nombreCompleto[1].trim() : "";

        Optional<Autor> autorExistente = autorRepository.findByNombreAndApellido(nombre, apellido);
        Autor autor;
        if (autorExistente.isPresent()) {
            autor = autorExistente.get();
        } else {
            autor = new Autor();
            autor.setNombre(nombre);
            autor.setApellido(apellido);
            autor = autorRepository.save(autor);
        }

        libro.setAutor(entityManager.merge(autor));
        libro = libroRepository.save(libro);
        libroRepository.flush();

        return libro;
    }

    public void eliminarTodosLosLibros() {
        libroRepository.deleteAll();
    }

    public List<LibroDTO> obtenerLibrosConCodigoYDescripcion() {
        List<Libro> libros = libroRepository.findAll();
        return libros.stream()
                .map(libro -> {
                    LibroDTO libroDTO = new LibroDTO();
                    libroDTO.setId(libro.getId());
                    libroDTO.setTitulo(libro.getTitulo());
                    Idioma idioma = libro.getIdioma();

                    String codigo = idioma.getCodigo();
                    String descripcion = idioma.getDescripcion();
                    libroDTO.setIdioma(codigo + " - " + descripcion);
                    return libroDTO;
                })
                .collect(Collectors.toList());
    }
}
