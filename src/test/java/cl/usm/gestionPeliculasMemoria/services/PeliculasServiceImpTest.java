package cl.usm.gestionPeliculasMemoria.services;

import cl.usm.gestionPeliculasMemoria.entities.Pelicula;
import cl.usm.gestionPeliculasMemoria.repositories.PeliculasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PeliculasServiceImplTest {

    @Mock
    private PeliculasRepository peliculasRepository;

    @InjectMocks
    private PeliculasServiceImpl peliculasService;

    private Pelicula pelicula1;
    private Pelicula pelicula2;

    @BeforeEach
    void setUp() {
        pelicula1 = new Pelicula("1", "Inception", "Nolan", null, null);
        pelicula2 = new Pelicula("2", "Titanic", "Cameron", null, null);
    }

    @Test
    @DisplayName("createPelicula - Debería generar token y guardar película")
    void createPelicula_ShouldGenerateTokenAndSave_WhenValid() {
        when(peliculasRepository.insert(any(Pelicula.class))).thenReturn(pelicula1);

        Pelicula result = peliculasService.createPelicula(pelicula1);

        assertNotNull(result);
        assertNotNull(result.getTokenDescarga());
        assertEquals(10, result.getTokenDescarga().length());
        verify(peliculasRepository, times(1)).insert(any(Pelicula.class));
    }

    @Test
    @DisplayName("createPelicula - Debería retornar null cuando hay excepción")
    void createPelicula_ShouldReturnNull_WhenExceptionOccurs() {
        when(peliculasRepository.insert(any(Pelicula.class))).thenThrow(new RuntimeException("Error"));

        Pelicula result = peliculasService.createPelicula(pelicula1);

        assertNull(result);
        verify(peliculasRepository, times(1)).insert(any(Pelicula.class));
    }

    @Test
    @DisplayName("getAll - Debería retornar todas las películas")
    void getAll_ShouldReturnAllPeliculas() {
        List<Pelicula> expected = Arrays.asList(pelicula1, pelicula2);
        when(peliculasRepository.findAll()).thenReturn(expected);

        List<Pelicula> result = peliculasService.getAll();

        assertEquals(2, result.size());
        verify(peliculasRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById - Debería retornar película cuando existe")
    void findById_ShouldReturnPelicula_WhenExists() {
        when(peliculasRepository.findById("1")).thenReturn(pelicula1);

        Pelicula result = peliculasService.findById("1");

        assertNotNull(result);
        assertEquals("Inception", result.getTitulo());
        verify(peliculasRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("findById - Debería retornar null cuando no existe")
    void findById_ShouldReturnNull_WhenNotExists() {
        when(peliculasRepository.findById("999")).thenReturn(null);

        Pelicula result = peliculasService.findById("999");

        assertNull(result);
        verify(peliculasRepository, times(1)).findById("999");
    }

    @Test
    @DisplayName("filter - Debería filtrar por ID (case insensitive)")
    void filter_ShouldFilterById_CaseInsensitive() {
        List<Pelicula> all = Arrays.asList(pelicula1, pelicula2);
        when(peliculasRepository.findAll()).thenReturn(all);

        List<Pelicula> result = peliculasService.filter("1");

        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
    }

    @Test
    @DisplayName("filter - Debería filtrar por título (case insensitive)")
    void filter_ShouldFilterByTitulo_CaseInsensitive() {
        List<Pelicula> all = Arrays.asList(pelicula1, pelicula2);
        when(peliculasRepository.findAll()).thenReturn(all);

        List<Pelicula> result = peliculasService.filter("titanic");

        assertEquals(1, result.size());
        assertEquals("Titanic", result.get(0).getTitulo());
    }

    @Test
    @DisplayName("filter - Debería retornar lista vacía cuando no hay coincidencias")
    void filter_ShouldReturnEmptyList_WhenNoMatches() {
        List<Pelicula> all = Arrays.asList(pelicula1, pelicula2);
        when(peliculasRepository.findAll()).thenReturn(all);

        List<Pelicula> result = peliculasService.filter("xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("filter - Debería retornar múltiples coincidencias")
    void filter_ShouldReturnMultipleMatches() {
        Pelicula p3 = new Pelicula("10", "Inception 2", "Nolan", null, null);
        List<Pelicula> all = Arrays.asList(pelicula1, pelicula2, p3);
        when(peliculasRepository.findAll()).thenReturn(all);

        List<Pelicula> result = peliculasService.filter("inception");

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("filter - Debería funcionar con query vacía o null")
    void filter_ShouldHandleEmptyQuery() {
        List<Pelicula> all = Arrays.asList(pelicula1, pelicula2);
        when(peliculasRepository.findAll()).thenReturn(all);

        List<Pelicula> resultEmpty = peliculasService.filter("");
        List<Pelicula> resultNull = peliculasService.filter(null);

        assertEquals(2, resultEmpty.size());
        assertEquals(2, resultNull.size());
    }

    @Test
    @DisplayName("createPelicula - Debería preservar datos originales")
    void createPelicula_ShouldPreserveOriginalData() {
        when(peliculasRepository.insert(any(Pelicula.class))).thenAnswer(inv -> inv.getArgument(0));

        Pelicula result = peliculasService.createPelicula(pelicula1);

        assertEquals("1", result.getId());
        assertEquals("Inception", result.getTitulo());
        assertEquals("Nolan", result.getDirector());
    }
}
