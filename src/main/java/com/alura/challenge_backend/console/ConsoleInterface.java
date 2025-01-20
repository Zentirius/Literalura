package com.alura.challenge_backend.console;

import com.alura.challenge_backend.dto.AutorDTO;
import com.alura.challenge_backend.dto.LibroDTO;
import com.alura.challenge_backend.model.Autor;
import com.alura.challenge_backend.service.ApiLibroService;
import com.alura.challenge_backend.service.AutorService;
import com.alura.challenge_backend.service.LibroService;
import com.alura.challenge_backend.service.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleInterface implements CommandLineRunner {

    @Autowired
    private ApiLibroService apiLibroService;

    @Autowired
    private AutorService autorService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private EstadisticasService estadisticasService;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            mostrarMenu();
            int opcion = leerOpcion(scanner);

            switch (opcion) {
                case 1 -> buscarLibroPorAPI(scanner);
                case 2 -> agregarLibro(scanner);
                case 3 -> mostrarTodosLosLibros();
                case 4 -> filtrarLibrosPorIdioma(scanner);
                case 5 -> verTodosLosAutores();
                case 6 -> autoresVivosEnUnAnio(scanner);
                case 7 -> mostrarEstadisticasPorIdioma(scanner);
                case 8 -> eliminarLibroPorId(scanner); // Eliminar libro por ID
                case 9 -> limpiarBaseDeDatos();
                case 10 -> {
                    System.out.println("¡Gracias por usar el Catálogo de Literalura! Hasta la próxima.");
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("Opción no válida. Por favor, intente nuevamente.");
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("===============================================");
        System.out.println("|            MENÚ DEL CATÁLOGO LITERARIO       |");
        System.out.println("===============================================");
        System.out.println("|  1. Buscar libro por título en la API        |");
        System.out.println("|  2. Agregar libro a la base de datos         |");
        System.out.println("|  3. Mostrar todos los libros almacenados     |");
        System.out.println("|---------------------------------------------|");
        System.out.println("|  4. Filtrar libros por idioma en la API      |");
        System.out.println("|  5. Ver todos los autores almacenados        |");
        System.out.println("|  6. Autores vivos en un año específico       |");
        System.out.println("|     que se encuentren almacenados            |");
        System.out.println("|---------------------------------------------|");
        System.out.println("|  7. Estadísticas de libros por idioma        |");
        System.out.println("|---------------------------------------------|");
        System.out.println("|  8. Eliminar libro por ID                    |");
        System.out.println("|  9. Limpiar base de datos                    |");
        System.out.println("|---------------------------------------------|");
        System.out.println("| 10. Salir                                    |");
        System.out.println("===============================================");
        System.out.print("| Seleccione una opción: ");
    }

    private int leerOpcion(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            return -1; // Opción inválida
        }
    }

    private void buscarLibroPorAPI(Scanner scanner) {
        System.out.print("Ingrese el título del libro para buscar en la API: ");
        String titulo = scanner.nextLine();
        try {
            List<LibroDTO> libros = apiLibroService.buscarLibrosPorTitulo(titulo);
            if (libros.isEmpty()) {
                System.out.println("No se encontraron libros con ese título en la API.");
            } else {
                System.out.println("Resultados encontrados:");
                libros.forEach(libro -> {
                    System.out.println("ID: " + libro.getId());
                    System.out.println("Título: " + libro.getTitulo());
                    System.out.println("Autor: " + libro.getAutor().getNombre());
                    System.out.println("Idioma: " + libro.getIdioma());
                    System.out.println("Número de descargas: " + libro.getNumeroDescargas());
                    System.out.println("-----------------------------------");
                });
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al buscar libros en la API: " + e.getMessage());
        }
    }

    private void agregarLibro(Scanner scanner) {
        System.out.print("Ingrese el título del libro para buscar en la API: ");
        String titulo = scanner.nextLine();
        try {
            List<LibroDTO> libros = apiLibroService.buscarLibrosPorTitulo(titulo);
            if (libros.isEmpty()) {
                System.out.println("No se encontraron libros con ese título en la API.");
            } else {
                System.out.println("Resultados encontrados:");
                libros.forEach(libro -> {
                    System.out.println("ID: " + libro.getId());
                    System.out.println("Título: " + libro.getTitulo());
                    System.out.println("Autor: " + libro.getAutor().getNombre());
                    System.out.println("Idioma: " + libro.getIdioma());
                    System.out.println("Número de descargas: " + libro.getNumeroDescargas());
                    System.out.println("-----------------------------------");
                });

                System.out.print("Ingrese el ID del libro que desea agregar: ");
                Long id = Long.parseLong(scanner.nextLine());

                LibroDTO libroSeleccionado = libros.stream()
                        .filter(libro -> libro.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (libroSeleccionado == null) {
                    System.out.println("El ID ingresado no coincide con ningún libro de la lista.");
                    return;
                }

                apiLibroService.agregarLibro(libroSeleccionado);
                System.out.println("Libro añadido exitosamente.");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al buscar/agregar libro: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un ID válido.");
        }
    }

    private void mostrarTodosLosLibros() {
        List<LibroDTO> libros = libroService.mostrarTodosLosLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("===============================================");
            System.out.println("|            LISTA DE LIBROS                   |");
            System.out.println("===============================================");
            libros.forEach(libro -> {
                System.out.println("ID: " + libro.getId());
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + libro.getAutor().getNombre());
                System.out.println("Idioma: " + libro.getIdioma());
                System.out.println("Número de descargas: " + libro.getNumeroDescargas());
                String anioPublicacion = (libro.getAnioPublicacion() != null && libro.getAnioPublicacion() > 0)
                        ? libro.getAnioPublicacion().toString()
                        : "Desconocido";
                System.out.println("Año de publicación: " + anioPublicacion);
                System.out.println("-----------------------------------");
            });
        }
    }

    private void filtrarLibrosPorIdioma(Scanner scanner) {
        System.out.print("Ingrese el código del idioma: ");
        String codigo = scanner.nextLine();
        try {
            List<LibroDTO> libros = apiLibroService.filtrarLibrosPorIdioma(codigo);
            if (libros.isEmpty()) {
                System.out.println("No se encontraron libros en ese idioma.");
            } else {
                System.out.println("Listado de libros en " + codigo + ":");
                System.out.println("------------------------------------------------------------");
                libros.forEach(libro -> System.out.println(libro.toString()));
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al filtrar libros por idioma: " + e.getMessage());
        }
    }
    private void verTodosLosAutores() {
        List<AutorDTO> autores = autorService.listarTodosLosAutores();
        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores.");
        } else {
            System.out.println("===============================================");
            System.out.println("|            LISTA DE AUTORES                 |");
            System.out.println("===============================================");
            autores.forEach(autor -> {
                System.out.println("ID: " + autor.getId());
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Año de nacimiento: " + (autor.getAnioNacimiento() != null ? autor.getAnioNacimiento() : "Desconocido"));
                System.out.println("Año de fallecimiento: " + (autor.getAnioFallecimiento() != null ? autor.getAnioFallecimiento() : "Desconocido"));
                System.out.println("-----------------------------------");
            });
        }
    }

    private void autoresVivosEnUnAnio(Scanner scanner) {
        System.out.print("Ingrese el año para ver autores vivos: ");
        try {
            int year = Integer.parseInt(scanner.nextLine());
            List<Autor> autores = autorService.autoresVivos(year);
            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en el año " + year);
            } else {
                System.out.println("===============================================");
                System.out.println("|            AUTORES VIVOS EN " + year + "            |");
                System.out.println("===============================================");
                autores.forEach(autor -> System.out.println(autor.toString()));
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un año válido.");
        }
    }

    private void mostrarEstadisticasPorIdioma(Scanner scanner) {
        System.out.print("Ingrese el código del idioma: ");
        String codigo = scanner.nextLine();
        try {
            List<LibroDTO> libros = estadisticasService.obtenerLibrosPorIdioma(codigo);
            int totalLibros = libros.size();

            System.out.println("===============================================");
            System.out.println("|            ESTADÍSTICAS DE LIBROS           |");
            System.out.println("===============================================");
            System.out.println("|  Idioma: " + codigo.toUpperCase());
            System.out.println("|  Cantidad total de libros: " + totalLibros);
            System.out.println("===============================================");
            System.out.println("|  Listado de los primeros 10 libros:          |");
            System.out.println("===============================================");

            libros.stream().limit(10).forEach(libro -> {
                System.out.println(libro.toString());
            });

            if (totalLibros > 10) {
                System.out.println("===============================================");
                System.out.println("|  Nota: Mostrando los primeros 10 libros      |");
                System.out.println("|  de un total de " + totalLibros + " libros.                |");
                System.out.println("===============================================");
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error al mostrar estadísticas de libros por idioma: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Código de idioma no válido: " + e.getMessage());
        }
    }

    private void eliminarLibroPorId(Scanner scanner) {
        mostrarTodosLosLibros(); // Mostrar la lista de libros antes de eliminar

        System.out.print("Ingrese el ID del libro a eliminar: ");
        Long id = Long.parseLong(scanner.nextLine());
        try {
            libroService.eliminarLibroPorId(id);
            System.out.println("Libro eliminado exitosamente.");
            mostrarTodosLosLibros(); // Mostrar la lista de libros después de eliminar
        } catch (Exception e) {
            System.out.println("Error al eliminar el libro: " + e.getMessage());
        }
    }

    private void limpiarBaseDeDatos() {
        libroService.eliminarTodosLosLibros();
        autorService.eliminarTodosLosAutores();
        System.out.println("Base de datos limpiada con éxito.");
    }

}
