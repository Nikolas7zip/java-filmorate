package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, Set<Integer>> filmUserLikes = new HashMap<>();
    private int id = 0;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Integer id) {
        return films.get(id);
    }

    @Override
    public Film add(Film film) {
        film.setId(++id);
        films.put(id, film);

        return film;
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            return film;
        }

        return null;
    }

    @Override
    public void likeByUser(Integer filmId, Integer userId) {
        if (filmUserLikes.containsKey(filmId)) {
            filmUserLikes.get(filmId).add(userId);
        } else {
            Set<Integer> usersId = new HashSet<>();
            usersId.add(userId);
            filmUserLikes.put(filmId, usersId);
        }
    }

    @Override
    public boolean removeLikeByUser(Integer filmId, Integer userId) {
        if (filmUserLikes.containsKey(filmId)) {
            return filmUserLikes.get(filmId).remove(userId);
        }

        return false;
    }

    @Override
    public List<Film> getTopFilmsByLikes(int countLimit) {
        return films.values().stream()
                .sorted((f1, f2) -> {
                    Integer likes1 = (filmUserLikes.containsKey(f1.getId())) ? filmUserLikes.get(f1.getId()).size() : 0;
                    Integer likes2 = (filmUserLikes.containsKey(f2.getId())) ? filmUserLikes.get(f2.getId()).size() : 0;
                    return likes2.compareTo(likes1); // return reverse order
                })
                .limit(countLimit)
                .collect(Collectors.toList());
    }

    @Override
    public void throwIfNotFound(Integer id) throws ResourceNotFoundException {
        if (!films.containsKey(id)) {
            throw new ResourceNotFoundException("Not found film with id " + id);
        }
    }
}
