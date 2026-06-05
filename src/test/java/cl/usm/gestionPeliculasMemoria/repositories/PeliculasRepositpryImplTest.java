package cl.usm.gestionPeliculasMemoria.repositories;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PeliculasRepositoryImplTest {

    private PeliculasRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PeliculasRepositoryImpl();
    }

    @Test
    @DisplayName("Insert - Debería insertar una película correctamente")
    void insert_ShouldAddPelicula_WhenValid() {
        Pelicula pelicula = new Pelicula("1", "Inception", "Nolan", "token123", null);

        Pelicula result = repository.insert(pelicula);

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Inception", result.getTitulo());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("Insert - Debería lanzar excepción cuando ID es nulo")
    void insert_ShouldThrowException_WhenIdIsNull() {
        Pelicula pelicula = new Pelicula(null, "Titanic", "Cameron", "token123", null);

        assertThrows(IllegalArgumentException.class, () -> repository.insert(pelicula));
    }

    @Test
    @DisplayName("Insert - Debería lanzar excepción cuando ID ya existe")
    void insert_ShouldThrowException_WhenIdAlreadyExists() {
        Pelicula pelicula1 = new Pelicula("1", "Movie1", "Director1", "token1", null);
        Pelicula pelicula2 = new Pelicula("1", "Movie2", "Director2", "token2", null);

        repository.insert(pelicula1);

        assertThrows(IllegalArgumentException.class, () -> repository.insert(pelicula2));
    }

    @Test
    @DisplayName("Insert - Debería ser case insensitive para IDs duplicados")
    void insert_ShouldBeCaseInsensitive_WhenCheckingDuplicateId() {
        Pelicula pelicula1 = new Pelicula("abc", "Movie1", "Director1", "token1", null);
        Pelicula pelicula2 = new Pelicula("ABC", "Movie2", "Director2", "token2", null);

        repository.insert(pelicula1);

        assertThrows(IllegalArgumentException.class, () -> repository.insert(pelicula2));
    }

    @Test
    @DisplayName("findAll - Debería retornar lista vacía cuando no hay películas")
    void findAll_ShouldReturnEmptyList_WhenNoPeliculas() {
        var result = repository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAll - Debería retornar todas las películas almacenadas")
    void findAll_ShouldReturnAllPeliculas() {
        Pelicula p1 = new Pelicula("1", "Movie1", "Dir1", "tok1", null);
        Pelicula p2 = new Pelicula("2", "Movie2", "Dir2", "tok2", null);

        repository.insert(p1);
        repository.insert(p2);

        var result = repository.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getId().equals("1")));
        assertTrue(result.stream().anyMatch(p -> p.getId().equals("2")));
    }

    @Test
    @DisplayName("findAll - Debería retornar una copia independiente del storage")
    void findAll_ShouldReturnIndependentCopy() {
        Pelicula p = new Pelicula("1", "Movie", "Dir", "tok", null);
        repository.insert(p);

        var result = repository.findAll();
        result.clear();

        assertEquals(1, repository.findAll().size());
    }

    @Test
    @DisplayName("findById - Debería retornar película cuando ID existe")
    void findById_ShouldReturnPelicula_WhenIdExists() {
        Pelicula expected = new Pelicula("1", "Inception", "Nolan", "token123", null);
        repository.insert(expected);

        Pelicula result = repository.findById("1");

        assertNotNull(result);
        assertEquals("Inception", result.getTitulo());
    }

    @Test
    @DisplayName("findById - Debería ser case insensitive")
    void findById_ShouldBeCaseInsensitive() {
        Pelicula expected = new Pelicula("AbCd", "Movie", "Dir", "tok", null);
        repository.insert(expected);

        Pelicula result = repository.findById("aBcD");

        assertNotNull(result);
        assertEquals("AbCd", result.getId());
    }

    @Test
    @DisplayName("findById - Debería retornar null cuando ID no existe")
    void findById_ShouldReturnNull_WhenIdNotFound() {
        Pelicula result = repository.findById("nonexistent");

        assertNull(result);
    }

    @Test
    @DisplayName("findById - Debería retornar null cuando ID es null")
    void findById_ShouldReturnNull_WhenIdIsNull() {
        Pelicula result = repository.findById(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Insert - Debería permitir película con comentarios")
    void insert_ShouldAllowPeliculaWithComentarios() {
        Comentario[] comentarios = {
                new Comentario("user1", "Great movie!"),
                new Comentario("user2", "Awesome!")
        };
        Pelicula pelicula = new Pelicula("1", "Movie", "Dir", "tok", comentarios);

        Pelicula result = repository.insert(pelicula);

        assertNotNull(result);
        assertNotNull(result.getComentarios());
        assertEquals(2, result.getComentarios().length);
    }
}