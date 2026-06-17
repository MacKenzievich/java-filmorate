package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Repository
public class LikeBbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;
    private final SqlFileReader fileReader;

    private String addLikeSql;
    private String removeLikeSql;

    @PostConstruct
    public void init() {
        addLikeSql = fileReader.readSqlFile("/like/addLike.sql");
        removeLikeSql = fileReader.readSqlFile("/like/removeLike.sql");
    }

    @Override
    public void addLike(int id, int userId) {
        String sql = addLikeSql;
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void removeLike(int id, int userId) {
        String sql = removeLikeSql;
        jdbcTemplate.update(sql, id, userId);
    }
}