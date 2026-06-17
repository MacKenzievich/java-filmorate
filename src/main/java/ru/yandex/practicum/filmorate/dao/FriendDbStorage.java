package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SqlFileReader fileReader;

    private String addFriendSql;
    private String removeFriendSql;
    private String checkCountSql;
    private String updateStatusTrueSql;
    private String updateStatusFalseSql;
    private String findAllFriendsSql;
    private String findCommonFriendsSql;


    @PostConstruct
    public void init() {
        addFriendSql = fileReader.readSqlFile("/friend/addFriend.sql");
        removeFriendSql = fileReader.readSqlFile("/friend/removeFriend.sql");
        checkCountSql = fileReader.readSqlFile("/friend/checkStatus.sql");
        updateStatusTrueSql = fileReader.readSqlFile("/friend/updateStatusToTrue.sql");
        updateStatusFalseSql = fileReader.readSqlFile("/friend/updateStatusToFalse.sql");
        findAllFriendsSql = fileReader.readSqlFile("/friend/findAllFriends.sql");
        findCommonFriendsSql = fileReader.readSqlFile("/friend/findCommonFriends.sql");
    }

    @Override
    public void addFriend(int id, int friendId) {
        String sql = addFriendSql;
        jdbcTemplate.update(sql, id, friendId);
        checkStatus(id, friendId);
    }

    @Override
    public void removeFriend(int id, int friendId) {
        String sql = removeFriendSql;
        jdbcTemplate.update(sql, id, friendId);
        checkStatus(id, friendId);
    }

    private void checkStatus(int userId, int friendId) {
        String checkSql = checkCountSql;
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, friendId, userId);

        String updateSql;
        if (count > 0) {
            updateSql = updateStatusTrueSql;
            jdbcTemplate.update(updateSql, userId, friendId, friendId, userId);
        } else {
            updateSql = updateStatusFalseSql;
            jdbcTemplate.update(updateSql, userId, friendId);
        }
    }

    @Override
    public List<User> findAllFriends(int id) {
        String sql = findAllFriendsSql;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFriend(rs), id);
    }

    @Override
    public List<User> findCommonFriends(int id, int otherId) {
        String sql = findCommonFriendsSql;
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