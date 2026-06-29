package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReviewDbStorage implements ReviewStorage {
    private static final String SELECT_REVIEW_SQL = """
            SELECT r.review_id,
                   r.content,
                   r.is_positive,
                   r.user_id,
                   r.film_id,
                   COALESCE(SUM(rv.vote), 0) AS useful
            FROM reviews AS r
            LEFT JOIN review_votes AS rv ON r.review_id = rv.review_id
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sql = """
                INSERT INTO reviews (content, is_positive, user_id, film_id)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, new String[]{"review_id"});
            statement.setString(1, review.getContent());
            statement.setBoolean(2, review.getIsPositive());
            statement.setInt(3, review.getUserId());
            statement.setInt(4, review.getFilmId());
            return statement;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = """
                UPDATE reviews
                SET content = ?, is_positive = ?
                WHERE review_id = ?
                """;
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return findById(review.getReviewId()).orElse(review);
    }

    @Override
    public void delete(int reviewId) {
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
    }

    @Override
    public Optional<Review> findById(int reviewId) {
        String sql = SELECT_REVIEW_SQL + """
                WHERE r.review_id = ?
                GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), reviewId)
                .stream()
                .findFirst();
    }

    @Override
    public List<Review> findAll(Integer filmId, int count) {
        StringBuilder sql = new StringBuilder(SELECT_REVIEW_SQL);
        if (filmId != null) {
            sql.append(" WHERE r.film_id = ? ");
        }
        sql.append("""
                GROUP BY r.review_id, r.content, r.is_positive, r.user_id, r.film_id
                ORDER BY useful DESC, r.review_id ASC
                LIMIT ?
                """);
        if (filmId == null) {
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> makeReview(rs), count);
        }
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        saveVote(reviewId, userId, 1);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        saveVote(reviewId, userId, -1);
    }

    @Override
    public void deleteVote(int reviewId, int userId) {
        jdbcTemplate.update(
                "DELETE FROM review_votes WHERE review_id = ? AND user_id = ?",
                reviewId,
                userId
        );
    }

    private void saveVote(int reviewId, int userId, int vote) {
        String sql = """
                MERGE INTO review_votes (review_id, user_id, vote)
                KEY (review_id, user_id)
                VALUES (?, ?, ?)
                """;
        jdbcTemplate.update(sql, reviewId, userId, vote);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
