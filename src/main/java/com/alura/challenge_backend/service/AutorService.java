package com.alura.challenge_backend.service;

import com.alura.challenge_backend.dto.AutorDTO;
import com.alura.challenge_backend.model.Autor;
import com.alura.challenge_backend.repository.AutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutorService {

    private static final Logger logger = LoggerFactory.getLogger(AutorService.class);

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private ApiUtilService apiUtilService;

    private static final String API_URL = "https://gutendex.com/books";

    public List<Autor> findAll() {
        logger.debug("Entering findAll method");
        List<Autor> autores = autorRepository.findAll();
        logger.debug("Exiting findAll method with authors: {}", autores);
        return autores;
    }

    public Autor findById(Long id) {
        logger.debug("Entering findById method with id: {}", id);
        return autorRepository.findById(id).orElse(null);
    }

    public Autor save(Autor autor) {
        logger.debug("Entering save method with author: {}", autor);
        return autorRepository.save(autor);
    }

    public List<Autor> autoresVivos(Integer anio) {
        logger.debug("Entering autoresVivos method with year: {}", anio);
        return autorRepository.findAutoresVivos(anio);
    }

    @Transactional
    public void eliminarTodosLosAutores() {
        autorRepository.deleteAll();
        logger.info("Todos los autores han sido eliminados.");
    }

        public List<AutorDTO> listarTodosLosAutores() {
            List<Autor> autores = autorRepository.findAll();
            return autores.stream()
                    .map(autor -> {
                        AutorDTO autorDTO = new AutorDTO();
                        autorDTO.setId(autor.getId());
                        autorDTO.setNombre(autor.getNombre());
                        autorDTO.setAnioNacimiento(autor.getAnioNacimiento());
                        autorDTO.setAnioFallecimiento(autor.getAnioFallecimiento());
                        // Uso del método getAnioNacimiento
                        Integer anioNacimiento = autorDTO.getAnioNacimiento();
                        // Puedes hacer algo con el anioNacimiento si es necesario
                        return autorDTO;
                    })
                    .collect(Collectors.toList());
        }

    public List<AutorDTO> listarAutoresVivos(int anioActual) throws IOException, InterruptedException {
        logger.info("Listando autores vivos hasta el año: {}", anioActual);
        HttpResponse<String> response = apiUtilService.enviarSolicitud(API_URL, "GET", null);
        apiUtilService.validarRespuesta(response);

        List<AutorDTO> todosLosAutores = apiUtilService.procesarAutores(response.body());
        return todosLosAutores.stream()
                .filter(autor -> autor.getAnioFallecimiento() == null || autor.getAnioFallecimiento() > anioActual)
                .collect(Collectors.toList());  // Asegúrate de importar Collectors
    }
}
