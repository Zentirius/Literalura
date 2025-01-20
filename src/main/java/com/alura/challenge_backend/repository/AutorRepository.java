package com.alura.challenge_backend.repository;

import com.alura.challenge_backend.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNombreAndApellido(String nombre, String apellido);

    @Query("SELECT a FROM Autor a WHERE a.anioNacimiento <= :anio AND (a.anioFallecimiento IS NULL OR a.anioFallecimiento > :anio)")
    List<Autor> findAutoresVivos(@Param("anio") Integer anio);
}
