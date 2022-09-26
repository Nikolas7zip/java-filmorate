package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private Film film;

    @BeforeEach
    public void beforeEach() {
        film = Film.builder()
                .name("Inception")
                .description("Short description")
                .releaseDate(LocalDate.of(2010, 7, 22))
                .duration(148)
                .mpa(new MpaRating(6, "Боевик"))
                .build();
    }

    @Test
    public void shouldBeCorrectFilm() {
        assertEquals(0, validator.validate(film).size());
    }

    @Test
    public void shouldCheckNullName() {
        film.setName(null);
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    public void shouldCheckEmptyName() {
        film.setName(" ");
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    public void shouldCheckEarlyReleaseDate() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    public void shouldCheckLongDescription() {
        String longDescription = "A thief who steals corporate secrets through the use of " +
                "dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., " +
                "but his tragic past may doom the project and his team to disaster.";
        film.setDescription(longDescription);
        assertEquals(1, validator.validate(film).size());
    }

    @Test
    public void shouldCheckNegativeDuration() {
        film.setDuration(-1);
        assertEquals(1, validator.validate(film).size());
    }
}
