package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikeStorage;


@RequiredArgsConstructor
@Repository
public class LikeBbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String ADD_LIKE_SQL = """
            INSERT INTO likes (film_id, user_id) VALUES (?, ?)
            """;
    private static final String REMOVE_LIKE_SQL = """
            DELETE FROM likes WHERE film_id = ? AND user_id = ?
            """;


    @Override
    public void addLike(int id, int userId) {
        String sql = ADD_LIKE_SQL;
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void removeLike(int id, int userId) {
        String sql = REMOVE_LIKE_SQL;
        jdbcTemplate.update(sql, id, userId);
    }
}