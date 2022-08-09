package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_LENGTH_DESCRIPTION = 200;

    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Film newFilm) {
        try {
            validate(newFilm);
            newFilm.setId(++id);
            films.put(id, newFilm);
            log.info("Success create new film: {}", newFilm);
        } catch (ValidationException ex) {
            log.warn("Fail create film " + newFilm + ": " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return new ResponseEntity<>(newFilm, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Film updatedFilm) {
        try {
            int filmId = updatedFilm.getId();
            if (films.containsKey(filmId)) {
                validate(updatedFilm);
                films.put(filmId, updatedFilm);
                log.info("Success update film: {}", updatedFilm);
            } else {
                log.warn("Fail update film " + updatedFilm + ": Not found film with id " + filmId);
                return ResponseEntity.notFound().build();
            }
        } catch (ValidationException ex) {
            log.warn("Fail update film " + updatedFilm + ": " + ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }

    public static void validate(final Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            throw new ValidationException("Empty film name");
        } else if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidationException("Long film description: " + film.getDescription().length() + " characters");
        } else if (CINEMA_BIRTHDAY.isAfter(film.getReleaseDate())) {
            throw new ValidationException("Wrong film release date (before cinema birthday):" + film.getReleaseDate());
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Wrong film duration: " + film.getDuration());
        }
    }
}
