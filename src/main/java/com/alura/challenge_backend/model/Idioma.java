package com.alura.challenge_backend.model;


public enum Idioma {
    ESPANOL("es", "Español"),
    INGLES("en", "Inglés"),
    PORTUGUES("pt", "Portugués"),
    FRANCES("fr", "Francés"),
    ITALIANO("it", "Italiano"),
    ALEMAN("de", "Alemán"),
    HOLANDES("nl", "Holandés"); // Añadido Holandés

    private final String codigo;
    private final String descripcion;

    Idioma(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static Idioma fromCodigo(String codigo) {
        switch (codigo.toLowerCase()) {
            case "es": case "español": return ESPANOL;
            case "en": case "inglés": return INGLES;
            case "pt": case "portugués": return PORTUGUES;
            case "fr": case "francés": return FRANCES;
            case "it": case "italiano": return ITALIANO;
            case "de": case "alemán": return ALEMAN;
            case "nl": case "holandés": return HOLANDES;
            default: throw new IllegalArgumentException("Idioma no reconocido: " + codigo);
        }
    }
}
