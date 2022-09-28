package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> getAll();
    Optional<Genre> getById(int id);
    List<Genre> getFilmGenre(long filmId);
    boolean addGenreForFilm(int genreId, long filmId);
    boolean removeGenreForFilm(int genreId, long filmId);
    Map<Long, List<Genre>> getGenresForAllFilms();
    Map<Long, List<Genre>> getGenresForSpecificFilms(List<Long> filmsId);
}
