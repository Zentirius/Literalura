package com.alura.challenge_backend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LibroDTO {
    private Long id;
    private String titulo;
    private String idioma;
    private Integer numeroDescargas;
    private Integer anioPublicacion;
    private AutorDTO autor;

    // Constructor por defecto
    public LibroDTO() {}

    // Constructor con parámetros
    public LibroDTO(Long id, String titulo, String idioma, Integer numeroDescargas, Integer anioPublicacion, AutorDTO autor) {
        this.id = id;
        this.titulo = titulo;
        this.idioma = idioma;
        this.numeroDescargas = numeroDescargas;
        this.anioPublicacion = anioPublicacion;
        this.autor = autor;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNumeroDescargas() {
        return numeroDescargas;
    }

    public void setNumeroDescargas(Integer numeroDescargas) {
        this.numeroDescargas = numeroDescargas;
    }

    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public AutorDTO getAutor() {
        return autor;
    }

    public void setAutor(AutorDTO autor) {
        this.autor = autor;
    }
    @Override
    public String toString() {
        return "ID: " + id +
                "\nTítulo: " + titulo +
                "\nAutor: " + (autor != null ? autor.getNombre() : "Información no disponible") +
                "\nIdioma: " + (idioma != null ? idioma : "Información no disponible") +
                "\nNúmero de descargas: " + (numeroDescargas != null ? numeroDescargas : "Información no disponible") +
                "\nAño de publicación: " + (anioPublicacion != null && anioPublicacion != 0 ? anioPublicacion : "Información no disponible") +
                "\n-----------------------------------";
    }




}
