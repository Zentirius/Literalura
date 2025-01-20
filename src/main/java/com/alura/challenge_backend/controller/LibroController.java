package com.alura.challenge_backend.controller;

import com.alura.challenge_backend.dto.LibroDTO;
import com.alura.challenge_backend.model.Libro;
import com.alura.challenge_backend.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping("/titulo/{titulo}")
    public List<Libro> buscarPorTitulo(@PathVariable String titulo) {
        return libroService.buscarLibrosPorTitulo(titulo);
    }

    @GetMapping
    public List<Libro> listarLibros() {
        return libroService.listarTodosLosLibros();
    }

    @GetMapping("/api")
    public List<LibroDTO> buscarLibrosDesdeAPI(@RequestParam String queryParams) throws IOException, InterruptedException {
        return libroService.buscarLibrosDesdeAPI(queryParams);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        libroService.eliminarLibroPorId(id);
    }

    @PostMapping("/agregar")
    public ResponseEntity<Libro> agregarLibro(@RequestBody LibroDTO libroDTO) {
        return ResponseEntity.ok(libroService.agregarLibro(libroDTO));
    }

    @GetMapping("/todos")
    public List<LibroDTO> obtenerTodosLosLibros() {
        return libroService.mostrarTodosLosLibros();
    }
}
