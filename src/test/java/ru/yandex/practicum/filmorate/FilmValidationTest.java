package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FilmValidationTest {

    @Test
    public void shouldNotThrowExceptionOnCorrectFilm() {
        Film film = new Film("Inception",  "Short description", LocalDate.of(2010, 7, 22), 148);
        assertDoesNotThrow(() -> {
            FilmController.validate(film);
        });
    }

    @Test
    public void shouldThrowNullPointerExceptionForNullName() {
        final NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> {
                    Film film = new Film(null,  "Short description", LocalDate.of(2010, 7, 22), 148);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForEmptyName() {
        Film film = new Film("", "Description", LocalDate.now(), 120);

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    FilmController.validate(film);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForEarlyReleaseDate() {
        Film film = new Film("Inception", "Description", LocalDate.of(1850, 1, 10), 120);

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    FilmController.validate(film);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForLongDescription() {
        String longDescription = "A thief who steals corporate secrets through the use of " +
                "dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., " +
                "but his tragic past may doom the project and his team to disaster.";
        Film film = new Film("Inception", longDescription, LocalDate.of(2010, 7, 22), 148);

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    FilmController.validate(film);
                });
    }

    @Test
    public void shouldThrowValidationExceptionForNegativeDuration() {
        Film film = new Film("Inception",  "Short description", LocalDate.of(2010, 7, 22), -1);

        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> {
                    FilmController.validate(film);
                });
    }
}
