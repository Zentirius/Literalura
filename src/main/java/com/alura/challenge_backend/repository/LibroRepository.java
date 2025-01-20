package com.alura.challenge_backend.repository;

import com.alura.challenge_backend.model.Libro;
import com.alura.challenge_backend.model.Idioma;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    List<Libro> findByIdioma(Idioma idioma);
    long countByIdioma(Idioma idioma);  // Contará los libros por idioma
    @Modifying
    @Transactional
    @Query("DELETE FROM Libro l WHERE l.id = :id")
    void eliminarLibroPorIdManual(@Param("id") Long id);

}
