package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_FILMS_SQL = """
            SELECT f.film_id,
                   f.name,
                   f.description,
                   f.releaseDate,
                   f.duration,
                   mpa.rating_id,
                   mpa.name AS mpa_name
            FROM films AS f
            INNER JOIN mpa_rating AS mpa ON f.rating_id = mpa.rating_id
            """;
    private static final String INSERT_FILM_SQL = """
            INSERT INTO films (name, description, releaseDate, duration, rating_id)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String INSERT_FILM_GENRE_SQL = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?)
            """;
    private static final String SELECT_FILM_BY_ID_SQL = """
            WHERE f.film_id = ?
            """;
    private static final String DELETE_FILM_GENRES_SQL = """
            DELETE
            FROM film_genres
            WHERE film_id = ?
            """;
    private static final String SELECT_POPULAR_FILMS_SQL = """
            LEFT JOIN likes ON f.film_id = likes.film_id
            GROUP BY f.film_id
            ORDER BY COUNT(likes.film_id) DESC
            LIMIT ?
            """;
    private static final String UPDATE_FILM_SQL = """
            UPDATE films
            SET name        = ?,
                description = ?,
                releaseDate = ?,
                duration    = ?,
                rating_id   = ?
            WHERE film_id = ?
            """;
    private static final String SELECT_ALL_FILMS_SQL = """
            ORDER BY f.film_id
            """;
    private static final String DELETE_FILM_SQL = """
            DELETE FROM films WHERE film_id = ?
            """;


    @Override
    public Film create(Film film) {
        String sql = INSERT_FILM_SQL;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                    ps.setInt(4, film.getDuration());
                    ps.setInt(5, film.getMpa().getId());
                    return ps;
                }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        updateGenres(film.getGenres(), film.getId());
        return film;
    }


    @Override
    public Film update(Film film) {
        int id = film.getId();
        String sql = UPDATE_FILM_SQL;
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), id);
        updateGenres(film.getGenres(), id);
        return film;
    }

    @Override
    public List<Film> findAllFilms() {
        String sql = SELECT_ALL_FILMS_SQL;
        return jdbcTemplate.query(SELECT_FILMS_SQL + " " + sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> findFilmById(int id) {
        String sql = SELECT_FILM_BY_ID_SQL;
        return jdbcTemplate.query(SELECT_FILMS_SQL + " " + sql, (rs, rowNum) -> makeFilm(rs), id).stream().findFirst();
    }

    @Override
    public List<Film> findPopular(int count) {
        String sql = SELECT_POPULAR_FILMS_SQL;
        return jdbcTemplate.query(SELECT_FILMS_SQL + " " + sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    @Override
    public void deleteFilm(int id) {
        String sql = DELETE_FILM_SQL;
        jdbcTemplate.update(sql, id);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("film_id");
        Film film = Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releaseDate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("rating_id"), rs.getString("mpa_name")))
                .build();
        return film;
    }

    private void updateGenres(Set<Genre> genres, int id) {
        jdbcTemplate.update(DELETE_FILM_GENRES_SQL, id);
        if (genres.size() > 0) {
            String sql = INSERT_FILM_GENRE_SQL;
            Genre[] g = genres.toArray(new Genre[genres.size()]);
            jdbcTemplate.batchUpdate(
                    sql,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setInt(1, id);
                            ps.setInt(2, g[i].getId());
                        }

                        public int getBatchSize() {
                            return genres.size();
                        }
                    });
        }
    }


}