package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final LocalDate cinemaBirthday = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) throws BadRequestException {
        throwIfReleaseDateNotValid(newFilm);
        newFilm.setId(++id);
        films.put(id, newFilm);
        log.info("Success create {}", newFilm);
        return newFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updatedFilm) throws ResourceNotFoundException, BadRequestException {
        int filmId = updatedFilm.getId();
        if (films.containsKey(filmId)) {
            throwIfReleaseDateNotValid(updatedFilm);
            films.put(filmId, updatedFilm);
            log.info("Success update {}", updatedFilm);
        } else {
            String warningMessage = "Not found film with id " + filmId;
            log.warn(warningMessage);
            throw new ResourceNotFoundException(warningMessage);
        }
        return updatedFilm;
    }

    public void throwIfReleaseDateNotValid(final Film film) throws BadRequestException {
        if (cinemaBirthday.isAfter(film.getReleaseDate())) {
            String warningMessage = "Wrong film release date (before cinema birthday): " + film.getReleaseDate();
            log.warn(warningMessage);
            throw new BadRequestException(warningMessage);
        }
    }
}
