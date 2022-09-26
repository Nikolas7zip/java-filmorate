package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    List<Genre> getAll();
    Genre getById(int id);
    List<Genre> getFilmGenre(int filmId);
    boolean addGenreForFilm(int genreId, int filmId);
    boolean removeGenreForFilm(int genreId, int filmId);
}
