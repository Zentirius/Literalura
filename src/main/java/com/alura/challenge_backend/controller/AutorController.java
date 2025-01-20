package com.alura.challenge_backend.controller;

import com.alura.challenge_backend.model.Autor;
import com.alura.challenge_backend.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @GetMapping
    public List<Autor> listarAutores() {
        return autorService.findAll();
    }

    @GetMapping("/vivos/{anio}")
    public ResponseEntity<?> listarAutoresVivos(@PathVariable Integer anio) {
        List<Autor> autores = autorService.autoresVivos(anio);
        if (autores.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontraron autores vivos en el año " + anio);
        }
        return ResponseEntity.ok(autores);
    }
}
