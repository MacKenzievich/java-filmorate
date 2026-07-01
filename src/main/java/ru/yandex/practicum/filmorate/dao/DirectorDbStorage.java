package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_DIRECTORS_SQL = """
            SELECT *
            FROM directors
            """;
    private static final String SELECT_DIRECTORS_BY_ID_SQL = SELECT_DIRECTORS_SQL + """
            WHERE director_id = ?
            """;
    private static final String INSERT_DIRECTOR_SQL = """
            INSERT INTO directors (director_name) VALUES(?)
            """;
    private static final String UPDATE_DIRECTOR_SQL = """
            UPDATE directors SET director_name = ? WHERE director_id = ?
            """;
    private static final String DELETE_DERECTOR_BY_ID_SQL = """
            DELETE FROM directors
            WHERE director_id = ?
            """;

    @Override
    public Collection<Director> findAllDirectors() {
        String sql = SELECT_DIRECTORS_SQL;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Optional<Director> findDirectorById(int id) {
        String sql = SELECT_DIRECTORS_BY_ID_SQL;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id).stream().findFirst();
    }

    @Override
    public Director createDirector(Director director) {
        String sql = INSERT_DIRECTOR_SQL;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"director_id"});
                    ps.setString(1, director.getName());
                    return ps;
                }, keyHolder);
        director.setId(keyHolder.getKey().intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = UPDATE_DIRECTOR_SQL;
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirectorById(int id) {
        String sql = DELETE_DERECTOR_BY_ID_SQL;
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void findAllDirectorsByFilm(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }
        final Map<Integer, Film> filmById = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film));

        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));

        String sql = "SELECT fd.film_id, d.director_id, d.director_name "
                + "FROM film_director fd "
                + "JOIN directors d ON fd.director_id = d.director_id "
                + "WHERE fd.film_id IN (" + inSql + ")";

        jdbcTemplate.query(
                sql,
                filmById.keySet().toArray(),
                (rs, rowNum) -> {
                    int filmId = rs.getInt("film_id");
                    Film film = filmById.get(filmId);
                    if (film != null) {
                        Director director = makeDirector(rs);
                        if (director != null) {
                            film.getDirectors().add(director);
                        }
                    }
                    return null;
                }
        );

    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }

}
