package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SqlFileReader fileReader;

    private String insertUserSql;
    private String selectAllUsersSql;
    private String selectUserByIdSql;
    private String updateUserSql;


    @PostConstruct
    public void init() {
        insertUserSql = fileReader.readSqlFile("/user/insertUser.sql");
        selectAllUsersSql = fileReader.readSqlFile("/user/selectAllUsers.sql");
        selectUserByIdSql = fileReader.readSqlFile("/user/selectUserById.sql");
        updateUserSql = fileReader.readSqlFile("/user/updateUser.sql");
    }

    @Override
    public User create(User user) {
        String sql = insertUserSql;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, Date.valueOf(user.getBirthday()));
                    return ps;
                }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = updateUserSql;
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> findAll() {
        String sql = selectAllUsersSql;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> findUserById(int id) {
        String sql = selectUserByIdSql;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id).stream().findFirst();
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}