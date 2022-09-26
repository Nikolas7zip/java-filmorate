package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAll();
        for (Film film : films) {
            film.setGenres(genreStorage.getFilmGenre(film.getId()));
        }

        return films;
    }

    public Film getFilmById(int filmId) throws ResourceNotFoundException {
        Film film = filmStorage.getById(filmId);
        if (film == null) {
            throw new ResourceNotFoundException("Not found film with id " + filmId);
        }
        film.setGenres(genreStorage.getFilmGenre(filmId));

        return film;
    }

    public List<Film> getPopularFilms(int limitFilms) {
        List<Film> films = filmStorage.getPopularFilms(limitFilms);
        for (Film film : films) {
            film.setGenres(genreStorage.getFilmGenre(film.getId()));
        }

        return films;
    }

    public Film createFilm(Film film) {
        int filmId = filmStorage.add(film);
        addFilmGenres(filmId, film.getGenres());
        Film filmFromStorage = getFilmById(filmId);
        log.info("Success create {}", filmFromStorage);

        return filmFromStorage;
    }

    public Film updateFilm(Film film) throws ResourceNotFoundException {
        if (filmStorage.getById(film.getId()) == null) {
            throw new ResourceNotFoundException("Not found film with id " + film.getId());
        }
        filmStorage.update(film);
        updateFilmGenres(film.getId(), film.getGenres());
        Film filmFromStorage = getFilmById(film.getId());
        log.info("Success update {}", filmFromStorage);

        return filmFromStorage;
    }

    public void likeFilmByUser(int filmId, int userId) throws ResourceNotFoundException, BadRequestException {
        throwIfFilmOrUserNotFound(filmId, userId);
        boolean isFilmLiked = filmStorage.likeFilmByUser(filmId, userId);
        if (isFilmLiked) {
            log.info("User " + userId + " liked film with id " + filmId);
        } else {
            throw new BadRequestException("Can't like film " + filmId + " by user " + userId);
        }
    }

    public void removeLikeFromFilmByUser(int filmId, int userId) throws ResourceNotFoundException, BadRequestException {
        throwIfFilmOrUserNotFound(filmId, userId);
        boolean isLikeRemoved = filmStorage.removeLikeFromFilmByUser(filmId, userId);
        if (isLikeRemoved) {
            log.info("User " + userId + " remove like from film with id " + filmId);
        } else {
            throw new BadRequestException("Can't remove like from film with id " + filmId + " by user " + userId);
        }
    }

    private void throwIfFilmOrUserNotFound(int filmId, int userId) throws ResourceNotFoundException {
        if (filmStorage.getById(filmId) == null) {
            throw new ResourceNotFoundException("Not found film with id " + filmId);
        }

        if (userStorage.getById(userId) == null) {
            throw new ResourceNotFoundException("Not found user with id " + userId);
        }
    }

    private void addFilmGenres(int filmId, List<Genre> genres) {
        if (genres != null) {
            Set<Genre> uniqueGenres = new HashSet<>(genres);
            for (Genre g : uniqueGenres) {
                genreStorage.addGenreForFilm(g.getId(), filmId);
            }
        }
    }

    private Set<Integer> getGenresId(List<Genre> genres) {
        if (genres != null) {
            return genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    private void updateFilmGenres(int filmId, List<Genre> newGenres) {
        Set<Integer> newGenresId = getGenresId(newGenres);
        Set<Integer> dbGenresId = getGenresId(genreStorage.getFilmGenre(filmId));
        for (Integer dbGenreId : dbGenresId) {
            if (!newGenresId.contains(dbGenreId)) {
                genreStorage.removeGenreForFilm(dbGenreId, filmId);
            }
        }
        for (Integer newGenreId : newGenresId) {
            if (!dbGenresId.contains(newGenreId)) {
                genreStorage.addGenreForFilm(newGenreId, filmId);
            }
        }
    }
}
