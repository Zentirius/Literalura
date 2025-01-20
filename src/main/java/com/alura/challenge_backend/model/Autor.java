package com.alura.challenge_backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private Integer anioNacimiento;
    private Integer anioFallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ajuste aquí
    @JsonManagedReference
    private List<Libro> libros = new ArrayList<>();

    public Autor(String nombre, String apellido, Integer anioNacimiento, Integer anioFallecimiento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.anioNacimiento = anioNacimiento;
        this.anioFallecimiento = anioFallecimiento;
    }

    @Override
    public String toString() {
        return "-----------------------------------\n" +
                "ID: " + id + "\n" +
                "Nombre: " + nombre + '\n' +
                "Apellido: " + apellido + '\n' +
                "Año de nacimiento: " + (anioNacimiento != null ? anioNacimiento : "Desconocida") + '\n' +
                "Año de fallecimiento: " + (anioFallecimiento != null ? anioFallecimiento : "Desconocida") + '\n';
    }
}
