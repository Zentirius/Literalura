package com.alura.challenge_backend.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class IdiomaConverter implements AttributeConverter<Idioma, String> {
    @Override
    public String convertToDatabaseColumn(Idioma attribute) {
        return attribute.getDescripcion(); // Guarda la descripción en la base de datos
    }

    @Override
    public Idioma convertToEntityAttribute(String dbData) {
        return Idioma.fromCodigo(dbData); // Usa el método fromCodigo
    }
}
