package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String ADD_FRIEND_SQL = """
            INSERT INTO friendship (user_id, friend_id, status)
            VALUES (?, ?, FALSE)
            """;
    private static final String REMOVE_FRIEND_SQL = """
            DELETE
            FROM friendship
            WHERE user_id = ?
              AND friend_id = ?
            """;
    private static final String CHECK_COUNT_SQL = """
            SELECT COUNT(*)
            FROM friendship
            WHERE user_id = ?
              AND friend_id = ?
            """;

    private static final String UPDATE_STATUS_TRUE_SQL = """
            UPDATE friendship
            SET status = TRUE
            WHERE (user_id = ? AND friend_id = ?)
               OR (user_id = ? AND friend_id = ?)
            """;
    private static final String UPDATE_STATUS_FALSE_SQL = """
            UPDATE friendship SET status = FALSE
            WHERE user_id = ? AND friend_id = ?
            """;
    private static final String FIND_ALL_FRIENDS_SQL = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
            FROM friendship AS f
                     INNER JOIN users AS u ON u.user_id = f.friend_id
            WHERE f.user_id = ?
            ORDER BY u.user_id
            """;
    private static final String FIND_COMMON_FRIENDS_SQL = """
            SELECT u.user_id, u.email, u.login, u.name, u.birthday
            FROM friendship AS f
                     INNER JOIN friendship fr ON fr.friend_id = f.friend_id
                     INNER JOIN users u ON u.user_id = fr.friend_id
            WHERE f.user_id = ?
              AND fr.user_id = ?
              AND f.friend_id <> fr.user_id
              AND fr.friend_id <> f.user_id
            """;


    @Override
    public void addFriend(int id, int friendId) {
        String sql = ADD_FRIEND_SQL;
        jdbcTemplate.update(sql, id, friendId);
        checkStatus(id, friendId);
    }

    @Override
    public void removeFriend(int id, int friendId) {
        String sql = REMOVE_FRIEND_SQL;
        jdbcTemplate.update(sql, id, friendId);
        checkStatus(id, friendId);
    }

    private void checkStatus(int userId, int friendId) {
        String checkSql = CHECK_COUNT_SQL;
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, friendId, userId);

        String updateSql;
        if (count > 0) {
            updateSql = UPDATE_STATUS_TRUE_SQL;
            jdbcTemplate.update(updateSql, userId, friendId, friendId, userId);
        } else {
            updateSql = UPDATE_STATUS_FALSE_SQL;
            jdbcTemplate.update(updateSql, userId, friendId);
        }
    }

    @Override
    public List<User> findAllFriends(int id) {
        String sql = FIND_ALL_FRIENDS_SQL;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFriend(rs), id);
    }

    @Override
    public List<User> findCommonFriends(int id, int otherId) {
        String sql = FIND_COMMON_FRIENDS_SQL;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFriend(rs), id, otherId);
    }

    private User makeFriend(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}