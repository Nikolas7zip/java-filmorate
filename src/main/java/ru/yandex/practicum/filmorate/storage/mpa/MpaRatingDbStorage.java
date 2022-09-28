package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class MpaRatingDbStorage implements MpaRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> getAll() {
        String sql = "SELECT * FROM mpa_rating";

        return jdbcTemplate.query(sql, this::mapRowToMpaRating);
    }

    @Override
    public Optional<MpaRating> getById(int id) {
        String sql = "SELECT * FROM mpa_rating WHERE id = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToMpaRating, id));
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB mpa_rating: Don't found mpa_rating id=" + id);
            return Optional.empty();
        }
    }

    private MpaRating mapRowToMpaRating(ResultSet resultSet, int rowNum) throws SQLException {
        return new MpaRating(resultSet.getInt("id"), resultSet.getString("name"));
    }

}
