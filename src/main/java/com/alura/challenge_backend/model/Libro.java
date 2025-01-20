package com.alura.challenge_backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "libros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String titulo;

    @Convert(converter = IdiomaConverter.class) // Usa el convertidor personalizado
    private Idioma idioma;

    @Column(name = "numero_descargas", nullable = false)
    private Integer numeroDescargas;

    @Column(name = "anio_publicacion")
    private Integer anioPublicacion;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE) // Ajuste aquí
    @JoinColumn(name = "autor_id", foreignKey = @ForeignKey(name = "libro_ibfk_1"))
    @JsonBackReference
    private Autor autor;

    public Libro(String titulo, Idioma idioma, int numeroDescargas, Autor autor) {
        this.titulo = titulo;
        this.idioma = idioma;
        this.numeroDescargas = numeroDescargas;
        this.autor = autor;
    }

    public Libro(Long id, String titulo, Idioma idioma, Integer numeroDescargas, Autor autor) {
        this.id = id;
        this.titulo = titulo;
        this.idioma = idioma;
        this.numeroDescargas = numeroDescargas;
        this.autor = autor;
    }

    @Override
    public String toString() {
        return "-----------------------------------\n" +
                "ID: " + id + "\n" +
                "Título: " + titulo + '\n' +
                "Autor: " + (autor != null ? autor.getNombre() : "Desconocido") + '\n' +
                "Idioma: " + (idioma != null ? idioma.getDescripcion() : "Desconocido") + '\n' +
                "Número de descargas: " + numeroDescargas + '\n' +
                "Año de publicación: " + (anioPublicacion != null ? anioPublicacion : "Desconocido") + '\n';
    }
}
