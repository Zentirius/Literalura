package com.alura.challenge_backend.service;

import com.alura.challenge_backend.dto.LibroDTO;
import com.alura.challenge_backend.repository.LibroRepository;
import com.alura.challenge_backend.model.Idioma;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class EstadisticasService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private ApiUtilService apiUtilService;

    private static final String API_URL = "https://gutendex.com/books";

    public long contarLibrosPorIdioma(String codigoIdioma) throws IOException, InterruptedException {
        Idioma idioma = Idioma.fromCodigo(codigoIdioma);
        // Podemos usar el método contarLibrosPorIdioma de ApiUtilService
        return apiUtilService.contarLibrosPorIdioma(idioma.getCodigo());
    }

    public List<LibroDTO> obtenerLibrosPorIdioma(String codigoIdioma) throws IOException, InterruptedException {
        Idioma idioma;
        try {
            idioma = Idioma.fromCodigo(codigoIdioma);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Código de idioma no reconocido: " + codigoIdioma);
        }
        return apiUtilService.obtenerLibrosPorIdioma(idioma.getCodigo());
    }




}
