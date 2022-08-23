package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAll();
    Film getById(Integer id);
    Film add(Film film);
    Film update(Film film);
    void likeByUser(Integer filmId, Integer userId);
    boolean removeLikeByUser(Integer filmId, Integer userId);
    List<Film> getTopFilmsByLikes(int countLimit);
    void throwIfNotFound(Integer id) throws ResourceNotFoundException;
}
