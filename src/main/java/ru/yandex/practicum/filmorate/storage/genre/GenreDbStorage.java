package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class GenreDbStorage implements GenreStorage{
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SElECT * FROM genre";

        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Optional<Genre> getById(int id) {
        String sql = "SELECT * FROM genre WHERE id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id));
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB genre: Don't found genre id=" + id);
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, List<Genre>> getGenresForAllFilms() {
        String sql = "SELECT fg.film_id, fg.genre_id, g.name " +
                     "FROM film_genre AS fg " +
                     "INNER JOIN genre AS g on g.id = fg.genre_id";

        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql);

        return rowsToFilmGenres(resultSet);
    }

    @Override
    public Map<Long, List<Genre>> getGenresForSpecificFilms(List<Long> filmsId) {
        String inParams = filmsId.stream().map(id -> "?").collect(Collectors.joining(","));;

        String sql = "SELECT fg.film_id, fg.genre_id, g.name " +
                "FROM film_genre AS fg " +
                "INNER JOIN genre AS g on g.id = fg.genre_id " +
                "WHERE fg.film_id IN (" + inParams + ")";
        SqlRowSet resultSet = jdbcTemplate.queryForRowSet(sql, filmsId.toArray());

        return rowsToFilmGenres(resultSet);
    }

    @Override
    public List<Genre> getFilmGenre(long filmId) {
        String sql = "SELECT g.id, g.name " +
                "FROM film_genre AS fg " +
                "INNER JOIN genre AS g on g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    @Override
    public boolean addGenreForFilm(int genreId, long filmId) {
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

        try {
            int rowInsert = jdbcTemplate.update(sql, filmId, genreId);
            return rowInsert == 1;
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB film_genre: fail insert genre_id=" + genreId + ", film_id=" + filmId);
            return false;
        }
    }

    @Override
    public boolean removeGenreForFilm(int genreId, long filmId) {
        String sql = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";

        try {
            int rowDeleted = jdbcTemplate.update(sql, filmId, genreId);
            return rowDeleted == 1;
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB film_genre: fail delete genre_id=" + genreId + ", film_id=" + filmId);
            return false;
        }
    }

    private Map<Long, List<Genre>> rowsToFilmGenres(SqlRowSet resultSet) {
        Map<Long, List<Genre>> filmsGenres = new HashMap<>();
        while (resultSet.next()) {
            Long filmId = resultSet.getLong("film_id");
            Genre genre = new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));
            if (filmsGenres.containsKey(filmId)) {
                filmsGenres.get(filmId).add(genre);
            } else {
                List<Genre> genres = new ArrayList<>();
                genres.add(genre);
                filmsGenres.put(filmId, genres);
            }
        }
        return filmsGenres;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
