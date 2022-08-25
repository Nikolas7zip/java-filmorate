package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new ResourceNotFoundException("Not found film with id " + filmId);
        }

        return film;
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> {
                    Integer likes1 = f1.getUserLikes().size();
                    Integer likes2 = f2.getUserLikes().size();
                    return likes2.compareTo(likes1); // select reverse order
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        setEmptyUserLikesSetIfNull(film);
        Film filmFromStorage = filmStorage.add(film);
        log.info("Success create {}", filmFromStorage);

        return filmFromStorage;
    }

    public Film updateFilm(Film film) throws ResourceNotFoundException {
        getFilmById(film.getId());      // check film is existed
        setEmptyUserLikesSetIfNull(film);
        Film filmFromStorage = filmStorage.update(film);
        log.info("Success update {}", filmFromStorage);

        return filmFromStorage;
    }

    public void likeFilmByUser(Integer filmId, Integer userId) throws ResourceNotFoundException {
        if (userStorage.getById(userId) == null) {
            throw new ResourceNotFoundException("Not found user with id " + userId);
        }

        getFilmById(filmId).getUserLikes().add(userId);
        log.info("User " + userId + " liked film with id " + filmId);
    }

    public void removeLikeFromFilmByUser(Integer filmId, Integer userId) throws ResourceNotFoundException {
        if (userStorage.getById(userId) == null) {
            throw new ResourceNotFoundException("Not found user with id " + userId);
        }

        if (getFilmById(filmId).getUserLikes().remove(userId)) {
            log.info("User " + userId + " remove like from film with id " + filmId);
        } else {
            throw new ResourceNotFoundException("Could not remove like from film with id " + filmId +
                    " by user " + userId);
        }
    }

    private void setEmptyUserLikesSetIfNull(Film film) {
        if (film.getUserLikes() == null) {
            film.setUserLikes(new HashSet<>());
        }
    }
}
