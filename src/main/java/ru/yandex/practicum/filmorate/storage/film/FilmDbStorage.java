package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT film.*, likes.num_likes, mpa.name AS mpa_name FROM film " +
                     "LEFT JOIN " +
                        "(SELECT film_id, COUNT(*) AS num_likes " +
                        "FROM film_like " +
                        "GROUP BY film_id) AS likes ON film.id=likes.film_id " +
                     "LEFT JOIN mpa_rating AS mpa ON film.mpa_id=mpa.id";

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getById(long id) {
        String sql = "SELECT film.*, mpa.name AS mpa_name,  " +
                             "(SELECT COUNT(*) FROM film_like " +
                             "WHERE film_like.film_id = film.id " +
                             "GROUP BY film_like.film_id) AS num_likes " +
                     "FROM film " +
                     "LEFT JOIN mpa_rating AS mpa ON film.mpa_id = mpa.id " +
                     "WHERE film.id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id));
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB film: Don't found film id=" + id);
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getPopularFilms(int limitFilms) {
        String sql = "SELECT film.*, likes.num_likes, mpa.name AS mpa_name FROM film " +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(*) AS num_likes " +
                "FROM film_like " +
                "GROUP BY film_id) AS likes ON film.id=likes.film_id " +
                "LEFT JOIN mpa_rating AS mpa ON film.mpa_id=mpa.id " +
                "ORDER BY likes.num_likes DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::mapRowToFilm, limitFilms);
    }

    @Override
    public long add(Film film) {
        String sql = "INSERT INTO film(name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, film.getName());
            pst.setString(2, film.getDescription());
            pst.setDate(3, Date.valueOf(film.getReleaseDate()));
            pst.setInt(4, film.getDuration());
            pst.setInt(5, film.getMpa().getId());
            return pst;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public void update(Film film) {
        String sql = "UPDATE film " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
    }

    @Override
    public boolean likeFilmByUser(long filmId, long userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";

        try {
            int rowInsert = jdbcTemplate.update(sql, filmId, userId);
            return rowInsert == 1;
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB film_like: fail insert film_id=" + filmId + ", user_id=" + userId);
            return false;
        }
    }

    @Override
    public boolean removeLikeFromFilmByUser(long filmId, long userId) {
        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";

        try {
            int rowDeleted = jdbcTemplate.update(sql, filmId, userId);
            return rowDeleted == 1;
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB film_like: fail delete film_id=" + filmId + ", user_id=" + userId);
            return false;
        }
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .numLikes(resultSet.getLong("num_likes"))
                .mpa(new MpaRating(resultSet.getInt("mpa_id"), resultSet.getString("mpa_name")))
                .build();
    }
}
