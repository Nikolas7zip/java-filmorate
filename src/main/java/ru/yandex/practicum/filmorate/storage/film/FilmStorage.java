package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();
    Film getById(int id);
    List<Film> getPopularFilms(int limitFilms);
    int add(Film film);
    void update(Film film);
    boolean likeFilmByUser(int filmId, int userId);
    boolean removeLikeFromFilmByUser(int filmId, int userId);
}
