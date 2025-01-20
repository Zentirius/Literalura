package com.alura.challenge_backend.controller;

import com.alura.challenge_backend.dto.AutorDTO;
import com.alura.challenge_backend.dto.LibroDTO;
import com.alura.challenge_backend.model.Libro;
import com.alura.challenge_backend.service.ApiLibroService;
import com.alura.challenge_backend.service.AutorService;
import com.alura.challenge_backend.service.EstadisticasService;
import com.alura.challenge_backend.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiLibroService apiLibroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private EstadisticasService estadisticasService;

    @Autowired
    private LibroService libroService;

    @GetMapping("/libros")
    public List<LibroDTO> buscarLibros(@RequestParam(required = false) String titulo) throws IOException, InterruptedException {
        if (titulo != null && !titulo.isEmpty()) {
            return apiLibroService.buscarLibrosPorTitulo(titulo);
        }
        return apiLibroService.listarTodosLosLibros();
    }

    @PostMapping("/libros")
    public ResponseEntity<LibroDTO> agregarLibro(@RequestBody LibroDTO libroDTO) throws IOException, InterruptedException {
        // Utilizar setAnioPublicacion aquí
        libroDTO.setAnioPublicacion(libroDTO.getAnioPublicacion());  // Asegurándonos de que se utiliza el setter
        Libro libroAgregado = libroService.agregarLibro(libroDTO);
        return ResponseEntity.ok(libroDTO);
    }

    @GetMapping("/autores")
    public List<AutorDTO> listarAutores() throws IOException, InterruptedException {
        // Llama al servicio para obtener la lista de autores y utiliza el método getAnioNacimiento
        List<AutorDTO> autores = autorService.listarTodosLosAutores();
        for (AutorDTO autor : autores) {
            // Uso del método getAnioNacimiento
            Integer anioNacimiento = autor.getAnioNacimiento();
            // Puedes hacer algo con el anioNacimiento si es necesario
        }
        return autores;
    }

    @GetMapping("/autores/vivos")
    public List<AutorDTO> listarAutoresVivos(@RequestParam int anio) throws IOException, InterruptedException {
        return autorService.listarAutoresVivos(anio);
    }

    @GetMapping("/libros/idioma")
    public List<LibroDTO> filtrarLibrosPorIdioma(@RequestParam String idioma) throws IOException, InterruptedException {
        return apiLibroService.filtrarLibrosPorIdioma(idioma);
    }

    @GetMapping("/libros/estadisticas")
    public long contarLibrosPorIdioma(@RequestParam String idioma) throws IOException, InterruptedException {
        return estadisticasService.contarLibrosPorIdioma(idioma);
    }

    @DeleteMapping("/libros/{id}")
    public ResponseEntity<Void> eliminarLibro(@PathVariable Long id) throws IOException, InterruptedException {
        apiLibroService.eliminarLibroPorId(id);
        return ResponseEntity.noContent().build();
    }

}
