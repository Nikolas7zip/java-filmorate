package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        if (count <= 0) {
            throw new BadRequestException("Count param should be positive");
        }

        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) {
        return filmService.createFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film updatedFilm) {
        return filmService.updateFilm(updatedFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilmByUser(@PathVariable("id") long filmId,
                               @PathVariable("userId") long userId) {
        filmService.likeFilmByUser(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromFilmByUser(@PathVariable("id") long filmId,
                                         @PathVariable("userId") long userId) {
        filmService.removeLikeFromFilmByUser(filmId, userId);
    }
}
