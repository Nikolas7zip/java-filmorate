package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
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
    public MpaRating getById(int id) {
        String sql = "SELECT * FROM mpa_rating WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToMpaRating, id);
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB mpa_rating: Don't found mpa_rating id=" + id);
            return null;
        }
    }

    private MpaRating mapRowToMpaRating(ResultSet resultSet, int rowNum) throws SQLException {
        return new MpaRating(resultSet.getInt("id"), resultSet.getString("name"));
    }

}
