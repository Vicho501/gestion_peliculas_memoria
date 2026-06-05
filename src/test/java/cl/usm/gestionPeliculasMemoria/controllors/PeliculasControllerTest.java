package cl.usm.gestionPeliculasMemoria.controllers;

import cl.usm.gestionPeliculasMemoria.entities.Comentario;
import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.services.PeliculasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculasControllerTest {

    @Mock
    private PeliculasService peliculasService;

    @InjectMocks
    private PeliculasController controller;

    private Pelicula pelicula1;
    private Pelicula pelicula2;
    private List<Pelicula> peliculasList;

    @BeforeEach
    void setUp() {
        pelicula1 = new Pelicula("1", "Inception", "Nolan", "token123", null);
        pelicula2 = new Pelicula("2", "Titanic", "Cameron", "token456", null);
        peliculasList = Arrays.asList(pelicula1, pelicula2);
    }

    // ========== GET /peliculas ==========

    @Test
    @DisplayName("GET /peliculas - Debería retornar todas las películas sin query")
    void getAll_ShouldReturnAllPeliculas_WhenNoQuery() {
        when(peliculasService.getAll()).thenReturn(peliculasList);

        ResponseEntity<List<Pelicula>> response = controller.getAll(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(peliculasService, times(1)).getAll();
        verify(peliculasService, never()).filter(any());
    }

    @Test
    @DisplayName("GET /peliculas - Debería retornar películas filtradas con query")
    void getAll_ShouldReturnFilteredPeliculas_WhenQueryProvided() {
        List<Pelicula> filtered = Arrays.asList(pelicula1);
        when(peliculasService.filter("inception")).thenReturn(filtered);

        ResponseEntity<List<Pelicula>> response = controller.getAll("inception");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(peliculasService, times(1)).filter("inception");
        verify(peliculasService, never()).getAll();
    }

    @Test
    @DisplayName("GET /peliculas - Debería ignorar query vacía")
    void getAll_ShouldIgnoreEmptyQuery() {
        when(peliculasService.getAll()).thenReturn(peliculasList);

        ResponseEntity<List<Pelicula>> response = controller.getAll("");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(peliculasService, times(1)).getAll();
        verify(peliculasService, never()).filter(any());
    }

    @Test
    @DisplayName("GET /peliculas - Debería retornar 500 cuando hay excepción")
    void getAll_ShouldReturn500_WhenExceptionOccurs() {
        when(peliculasService.getAll()).thenThrow(new RuntimeException("DB Error"));

        ResponseEntity<List<Pelicula>> response = controller.getAll(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    // ========== POST /peliculas ==========

    @Test
    @DisplayName("POST /peliculas - Debería crear película exitosamente")
    void createPelicula_ShouldReturnCreatedPelicula_WhenValid() {
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(pelicula1);

        ResponseEntity<?> response = controller.createPelicula(pelicula1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pelicula1, response.getBody());
        verify(peliculasService, times(1)).createPelicula(pelicula1);
    }

    @Test
    @DisplayName("POST /peliculas - Debería retornar 500 cuando creación falla")
    void createPelicula_ShouldReturn500_WhenCreationFails() {
        when(peliculasService.createPelicula(any(Pelicula.class))).thenReturn(null);

        ResponseEntity<?> response = controller.createPelicula(pelicula1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(peliculasService, times(1)).createPelicula(pelicula1);
    }

    // ========== GET /peliculas/{id} ==========

    @Test
    @DisplayName("GET /peliculas/{id} - Debería retornar película cuando existe")
    void findById_ShouldReturnPelicula_WhenExists() {
        when(peliculasService.findById("1")).thenReturn(pelicula1);

        ResponseEntity<Pelicula> response = controller.findById("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pelicula1, response.getBody());
        verify(peliculasService, times(1)).findById("1");
    }

    @Test
    @DisplayName("GET /peliculas/{id} - Debería retornar 404 cuando no existe")
    void findById_ShouldReturn404_WhenNotExists() {
        when(peliculasService.findById("999")).thenReturn(null);

        ResponseEntity<Pelicula> response = controller.findById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("GET /peliculas/{id} - Debería retornar 500 cuando hay excepción")
    void findById_ShouldReturn500_WhenExceptionOccurs() {
        when(peliculasService.findById("1")).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Pelicula> response = controller.findById("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ========== GET /peliculas/{id}/comentarios ==========

    @Test
    @DisplayName("GET /peliculas/{id}/comentarios - Debería retornar comentarios cuando existe")
    void getComentarios_ShouldReturnComentarios_WhenPeliculaExists() {
        Comentario[] comentarios = {
                new Comentario("user1", "Great!"),
                new Comentario("user2", "Awesome!")
        };
        pelicula1.setComentarios(comentarios);
        when(peliculasService.findById("1")).thenReturn(pelicula1);

        ResponseEntity<?> response = controller.getComentarios("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comentarios, response.getBody());
        verify(peliculasService, times(1)).findById("1");
    }

    @Test
    @DisplayName("GET /peliculas/{id}/comentarios - Debería retornar null comentarios cuando no hay")
    void getComentarios_ShouldReturnNull_WhenNoComentarios() {
        when(peliculasService.findById("1")).thenReturn(pelicula1);

        ResponseEntity<?> response = controller.getComentarios("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("GET /peliculas/{id}/comentarios - Debería retornar 404 cuando película no existe")
    void getComentarios_ShouldReturn404_WhenPeliculaNotExists() {
        when(peliculasService.findById("999")).thenReturn(null);

        ResponseEntity<?> response = controller.getComentarios("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("GET /peliculas/{id}/comentarios - Debería retornar 500 cuando hay excepción")
    void getComentarios_ShouldReturn500_WhenExceptionOccurs() {
        when(peliculasService.findById("1")).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = controller.getComentarios("1");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}