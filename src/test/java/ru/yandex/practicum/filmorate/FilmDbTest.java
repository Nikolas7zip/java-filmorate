package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/data.sql")
public class FilmDbTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userDbStorage;
    private static final Random randomize = new Random();

    @Test
    public void testGetFilmById() {
        // Arrange
        Film film = Film.builder()
                .name("Титаник")
                .description("Кораблекрушение")
                .releaseDate(LocalDate.of(1997, 12, 19))
                .duration(194)
                .mpa(new MpaRating(3, "PG-13"))
                .build();
        long filmId = filmStorage.add(film);
        film.setId(filmId);

        // Act
        Optional<Film> filmOptional = filmStorage.getById(filmId);

        // Assert
        assertTrue(filmOptional.isPresent());
        assertEquals(film, filmOptional.get());
    }

    @Test
    public void testFilmNotFound() {
        Optional<Film> filmOptional = filmStorage.getById(-1);
        assertTrue(filmOptional.isEmpty());
    }

    @Test
    public void testUpdateFilm() {
        // Arrange
        Film film = createTestFilm();
        film.setName("Cool Film");
        film.setDescription("About cool film");
        film.setDuration(100);

        // Act
        filmStorage.update(film);
        Optional<Film> filmOptional = filmStorage.getById(film.getId());

        // Assert
        assertTrue(filmOptional.isPresent());
        assertEquals(film, filmOptional.get());
    }

    @Test
    public void testLikeFilm() {
        Film film = createTestFilm();
        User user = createTestUser();

        filmStorage.likeFilmByUser(film.getId(), user.getId());
        Optional<Film> filmOptional = filmStorage.getById(film.getId());

        assertTrue(filmOptional.isPresent());
        assertEquals(1, filmOptional.get().getNumLikes());
    }

    @Test
    public void testRemoveLikeFilm() {
        Film film = createTestFilm();
        User user = createTestUser();

        filmStorage.likeFilmByUser(film.getId(), user.getId());
        filmStorage.removeLikeFromFilmByUser(film.getId(), user.getId());
        Optional<Film> filmOptional = filmStorage.getById(film.getId());

        assertTrue(filmOptional.isPresent());
        assertEquals(0, filmOptional.get().getNumLikes());
    }

    @Test
    public void testGetPopularFilms() {
        Film film1 = createTestFilm();
        Film film2 = createTestFilm();
        User user1 = createTestUser();
        User user2 = createTestUser();

        filmStorage.likeFilmByUser(film1.getId(), user1.getId());
        filmStorage.likeFilmByUser(film1.getId(), user2.getId());
        List<Film> films = filmStorage.getPopularFilms(2);
        film1.setNumLikes(2);
        assertEquals(film1, films.get(0));
    }

    @Test
    public void testGetAllFilms() {
        int numFilmsBefore = filmStorage.getAll().size();
        Film film1 = createTestFilm();
        Film film2 = createTestFilm();

        assertEquals(numFilmsBefore + 2, filmStorage.getAll().size());
    }

    private Film createTestFilm() {
        int numRandom = randomize.nextInt(100);
        Film film = Film.builder()
                .name("Film" + numRandom)
                .description("Short description " + numRandom)
                .releaseDate(LocalDate.of(2000, 1, 13))
                .duration(60 + numRandom)
                .mpa(new MpaRating(3, "PG-13"))
                .build();
        long filmId = filmStorage.add(film);
        film.setId(filmId);
        return film;
    }

    private User createTestUser() {
        User user = User.builder()
                .email("tester1@mail.ru")
                .login("Tester1")
                .name("Tester1")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        long userId = userDbStorage.add(user);
        return userDbStorage.getById(userId).get();
    }
}
