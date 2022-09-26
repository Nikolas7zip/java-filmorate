package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;


@Component("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM app_user";

        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM app_user WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB app_user: Don't found user id=" + id);
            return null;
        }
    }

    @Override
    public int add(User user) {
        String sql = "INSERT INTO app_user(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, user.getEmail());
            pst.setString(2, user.getLogin());
            pst.setString(3, user.getName());
            pst.setDate(4, Date.valueOf(user.getBirthday()));
            return pst;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE app_user " +
                     "SET email = ?, login = ?, name = ?, birthday = ? " +
                     "WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
    }

    @Override
    public boolean addToFriends(int userId, int friendId) {
        String sql = "INSERT INTO user_friend (user_id, friend_id) VALUES (?, ?)";

        try {
            int rowInsert = jdbcTemplate.update(sql, userId, friendId);
            return rowInsert == 1;
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB user_friend: fail insert user_id=" + userId + ", friend_id=" + friendId);
            return false;
        }
    }

    @Override
    public boolean removeFromFriends(int userId, int friendId) {
        String sql = "DELETE FROM user_friend WHERE user_id = ? AND friend_id = ?";

        try {
            int rowDeleted = jdbcTemplate.update(sql, userId, friendId);
            return rowDeleted == 1;
        } catch (DataAccessException ex) {
            log.warn(ex.toString());
            log.warn("DB user_friend: fail delete user_id=" + userId + ", friend_id=" + friendId);
            return false;
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT * FROM app_user WHERE id IN (SELECT friend_id FROM user_friend WHERE user_id = ?)";

        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int user1Id, int user2Id) {
        String sql = "SELECT * FROM app_user WHERE id IN " +
                "(SELECT friend_id FROM user_friend WHERE user_id = ? OR user_id = ? " +
                "GROUP BY friend_id HAVING COUNT(*) > 1)";

        return jdbcTemplate.query(sql, this::mapRowToUser, user1Id, user2Id);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
