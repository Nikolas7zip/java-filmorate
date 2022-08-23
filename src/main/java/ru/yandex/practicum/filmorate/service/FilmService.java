package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Integer filmId) throws ResourceNotFoundException {
        filmStorage.throwIfNotFound(filmId);

        return filmStorage.getById(filmId);
    }

    public List<Film> getPopularFilms(Integer count) throws BadRequestException {
        if (count == null || count <= 0) {
            throw new BadRequestException("count param should be positive integer");
        }

        return filmStorage.getTopFilmsByLikes(count);
    }

    public Film createFilm(Film film) {
        Film filmFromStorage = filmStorage.add(film);
        log.info("Success create {}", filmFromStorage);

        return filmFromStorage;
    }

    public Film updateFilm(Film film) throws ResourceNotFoundException {
        filmStorage.throwIfNotFound(film.getId());
        Film filmFromStorage = filmStorage.update(film);
        log.info("Success update {}", filmFromStorage);

        return filmFromStorage;
    }

    public void likeFilmByUser(Integer filmId, Integer userId) throws ResourceNotFoundException {
        filmStorage.throwIfNotFound(filmId);
        userStorage.throwIfNotFound(userId);
        filmStorage.likeByUser(filmId, userId);
        log.info("User " + userId + " liked film with id " + filmId);
    }

    public void removeLikeFromFilmByUser(Integer filmId, Integer userId) throws ResourceNotFoundException {
        filmStorage.throwIfNotFound(filmId);
        userStorage.throwIfNotFound(userId);
        if (filmStorage.removeLikeByUser(filmId, userId)) {
            log.info("User " + userId + " remove like from film with id " + filmId);
        } else {
            throw new ResourceNotFoundException("Could not remove like from film with id " + filmId +
                    " by user " + userId);
        }
    }
}
