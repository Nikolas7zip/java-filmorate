package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql("/data.sql")
public class GenreDbTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    public void testGetGenreById() {
        Genre genre = genreDbStorage.getById(1);

        assertEquals(1, genre.getId());
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAll();

        assertEquals(6, genres.size());
    }
}
