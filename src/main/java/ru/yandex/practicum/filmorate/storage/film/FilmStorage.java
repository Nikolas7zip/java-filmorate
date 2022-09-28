package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface FilmStorage extends Storage<Film> {
    List<Film> getPopularFilms(int limitFilms);
    boolean likeFilmByUser(long filmId, long userId);
    boolean removeLikeFromFilmByUser(long filmId, long userId);
}
